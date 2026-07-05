-- otlp_exporter.lua
-- Coleta métricas HTTP do OpenResty e envia via OTLP HTTP JSON para o OTel Collector.
-- Substitui o otel-collector-agent (sidecar), enviando as métricas diretamente do processo Nginx.
--
-- Funcionamento:
--   1. log_by_lua_block chama M.record() a cada requisição → atualiza contadores no shared dict
--   2. Um timer (ngx.timer.every) roda apenas no worker 0 e chama M.flush() periodicamente
--   3. flush() lê o shared dict, monta o payload OTLP JSON e faz POST para o collector

local _M = {}

-- ─── Configuração ──────────────────────────────────────────────────────────────

-- Lidos em runtime a partir das variáveis de ambiente definidas no Dockerfile/compose
local function get_config()
    return {
        endpoint      = os.getenv("OTEL_ENDPOINT") or "http://otel-collector:4318",
        service_name  = os.getenv("OTEL_SERVICE_NAME") or "proxy-openresty",
        flush_interval = tonumber(os.getenv("OTEL_FLUSH_INTERVAL")) or 30,  -- segundos
    }
end

-- ─── Shared Dict ───────────────────────────────────────────────────────────────
-- Usamos ngx.shared["otlp_metrics"] para acumular contadores entre workers.
-- Chaves:
--   "req:{status}:{path}:{method}" → contagem acumulada (integer)
--   "dur_sum:{path}"               → soma das durações em segundos (float * 1000, armazenado como int)
--   "dur_count:{path}"             → número de requisições com duração registrada

local DICT_NAME = "otlp_metrics"

local function get_dict()
    local d = ngx.shared[DICT_NAME]
    if not d then
        ngx.log(ngx.ERR, "[otlp_exporter] shared dict '", DICT_NAME, "' não encontrado. ",
                "Adicione 'lua_shared_dict otlp_metrics 5M;' no nginx.conf")
    end
    return d
end

-- ─── record() ─────────────────────────────────────────────────────────────────
-- Chamado a cada requisição no log_by_lua_block.
-- Incrementa o contador da combinação {status, path, method} e acumula a duração.

function _M.record(status, path, method, duration_s)
    local dict = get_dict()
    if not dict then return end

    -- Contador de requisições
    local req_key = "req:" .. status .. ":" .. path .. ":" .. method
    dict:incr(req_key, 1, 0)

    -- Duração: soma (armazenada em microssegundos para manter precisão como integer)
    if duration_s then
        local dur_sum_key   = "dur_sum:" .. path
        local dur_count_key = "dur_count:" .. path
        local us = math.floor(duration_s * 1e6)  -- converte para microssegundos inteiros
        dict:incr(dur_sum_key, us, 0)
        dict:incr(dur_count_key, 1, 0)
    end
end

-- ─── Helpers OTLP ─────────────────────────────────────────────────────────────

local function now_unix_nano()
    -- ngx.now() retorna segundos com milissegundos; convertemos para nanosegundos
    return string.format("%.0f", ngx.now() * 1e9)
end

local function make_attr(key, str_value)
    return { key = key, value = { stringValue = str_value } }
end

-- Monta um data point do tipo Sum (contador cumulativo) para o OTLP JSON
local function make_sum_datapoint(attrs, value, start_ns, now_ns)
    return {
        attributes          = attrs,
        startTimeUnixNano   = start_ns,
        timeUnixNano        = now_ns,
        asDouble            = value,
    }
end

-- ─── flush() ──────────────────────────────────────────────────────────────────
-- Chamado pelo timer no worker 0. Lê todos os contadores do shared dict,
-- monta o payload OTLP JSON e envia via HTTP POST para o collector.

function _M.flush(premature)
    if premature then return end  -- Nginx está desligando, não envia

    local dict = get_dict()
    if not dict then return end

    local cfg    = get_config()
    local now_ns = now_unix_nano()

    -- ── Coleta contadores de requisições ──────────────────────────────────────
    local req_datapoints = {}
    local dur_datapoints = {}

    local keys = dict:get_keys(0)  -- 0 = sem limite de chaves
    for _, key in ipairs(keys) do
        local value = dict:get(key)
        if not value then goto continue end

        -- Contadores de requisições: "req:{status}:{path}:{method}"
        if key:sub(1, 4) == "req:" then
            local rest = key:sub(5)
            -- Extrai status (3 chars) + path + method
            local status = rest:sub(1, 3)
            local remainder = rest:sub(5)  -- pula o ":" após status
            -- Divide no último ":" para separar path e method
            local last_colon = remainder:find(":[^:]*$")
            local path, method
            if last_colon then
                path   = remainder:sub(1, last_colon - 1)
                method = remainder:sub(last_colon + 1)
            else
                path   = remainder
                method = "UNKNOWN"
            end

            local attrs = {
                make_attr("status", status),
                make_attr("path", path),
                make_attr("method", method),
            }
            table.insert(req_datapoints, make_sum_datapoint(attrs, value, now_ns, now_ns))

        -- Duração: "dur_sum:{path}" e "dur_count:{path}"
        elseif key:sub(1, 8) == "dur_sum:" then
            local path      = key:sub(9)
            local sum_us    = value
            local count_key = "dur_count:" .. path
            local count     = dict:get(count_key) or 1

            -- Expõe como duas métricas: soma total (em segundos) e contagem
            local sum_s = sum_us / 1e6
            local attrs = { make_attr("path", path) }

            -- Gauge: duração média atual
            table.insert(dur_datapoints, {
                attributes    = attrs,
                timeUnixNano  = now_ns,
                asDouble      = sum_s / count,  -- latência média em segundos
            })
        end

        ::continue::
    end

    -- Se não há dados, não envia payload vazio
    if #req_datapoints == 0 and #dur_datapoints == 0 then
        ngx.log(ngx.DEBUG, "[otlp_exporter] nenhuma métrica para enviar")
        return
    end

    -- ── Monta payload OTLP JSON ───────────────────────────────────────────────
    local cjson   = require("cjson.safe")
    local metrics = {}

    if #req_datapoints > 0 then
        table.insert(metrics, {
            name        = "nginx_http_requests_total",
            description = "Número total de requisições HTTP por status, path e method",
            unit        = "1",
            sum         = {
                dataPoints              = req_datapoints,
                aggregationTemporality  = 2,   -- AGGREGATION_TEMPORALITY_CUMULATIVE
                isMonotonic             = true,
            },
        })
    end

    if #dur_datapoints > 0 then
        table.insert(metrics, {
            name        = "nginx_http_request_duration_avg_seconds",
            description = "Latência média das requisições HTTP por path",
            unit        = "s",
            gauge       = {
                dataPoints = dur_datapoints,
            },
        })
    end

    local payload = {
        resourceMetrics = {
            {
                resource = {
                    attributes = {
                        make_attr("service.name", cfg.service_name),
                        make_attr("telemetry.sdk.name", "openresty-lua"),
                    },
                },
                scopeMetrics = {
                    {
                        scope = {
                            name    = "otlp_exporter.lua",
                            version = "1.0.0",
                        },
                        metrics = metrics,
                    },
                },
            },
        },
    }

    local body, err = cjson.encode(payload)
    if not body then
        ngx.log(ngx.ERR, "[otlp_exporter] erro ao serializar JSON: ", err)
        return
    end

    -- ── Envia via HTTP POST ───────────────────────────────────────────────────
    local http = require("resty.http")
    local httpc = http.new()
    httpc:set_timeout(5000)  -- 5 segundos de timeout

    local url = cfg.endpoint .. "/v1/metrics"
    local res, err = httpc:request_uri(url, {
        method  = "POST",
        body    = body,
        headers = {
            ["Content-Type"] = "application/json",
        },
    })

    if not res then
        ngx.log(ngx.ERR, "[otlp_exporter] falha ao enviar para ", url, ": ", err)
        return
    end

    if res.status ~= 200 then
        ngx.log(ngx.WARN, "[otlp_exporter] collector retornou status ", res.status,
                " body: ", res.body)
    else
        ngx.log(ngx.DEBUG, "[otlp_exporter] flush enviado com sucesso — ",
                #req_datapoints, " séries de req, ",
                #dur_datapoints, " séries de duração")
    end
end

-- ─── init_timer() ─────────────────────────────────────────────────────────────
-- Agenda o timer de flush. Deve ser chamado no init_worker_by_lua_block.
-- Apenas o worker 0 dispara o timer para evitar envios duplicados.

function _M.init_timer()
    if ngx.worker.id() ~= 0 then
        return  -- apenas o worker 0 faz o flush
    end

    local cfg = get_config()
    local ok, err = ngx.timer.every(cfg.flush_interval, _M.flush)
    if not ok then
        ngx.log(ngx.ERR, "[otlp_exporter] falha ao agendar timer: ", err)
    else
        ngx.log(ngx.INFO, "[otlp_exporter] timer agendado a cada ",
                cfg.flush_interval, "s → ", cfg.endpoint)
    end
end

return _M
