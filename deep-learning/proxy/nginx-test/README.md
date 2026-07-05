# nginx-test — Proxy reverso OpenResty + Flask

Ambiente local de estudo de proxy reverso.

O **OpenResty** (nginx + LuaJIT) fica na beira recebendo as requisições e as repassa para um backend **Flask** simples. O foco do estudo é o proxy — o Flask é apenas um backend descartável.

```
Cliente → :8081 (OpenResty / proxy) → :5000 (Flask / backend)
```

---

## Estrutura

```
nginx-test/
├── docker-compose.yml              # modo normal: ambos os containers
├── docker-compose.host-debug.yml   # modo debug: só o proxy, Flask roda no host
├── test_proxy.py                   # script de validação automatizada
├── open-resty/
│   ├── Dockerfile
│   ├── entrypoint.sh               # substitui variáveis de env e inicia o nginx
│   └── nginx.conf.template         # config do nginx com placeholders
└── service/
    ├── Dockerfile
    ├── app.py                      # Flask com /health, /route-a, /route-b
    └── requirements.txt
```

---

## Pré-requisitos

- Docker (testado com `"bridge": "none"` e `"iptables": false` no `daemon.json`)
- Python 3.x (para rodar o Flask local ou o script de testes)

> **Nota sobre o ambiente:** O `daemon.json` deste host tem `"bridge": "none"`, o que desabilita a bridge padrão do Docker. Por isso, os blocos `build` nos Compose files usam `network: host` para que o `pip install` e outros comandos de build consigam acessar a internet. O runtime usa a rede interna criada pelo Compose normalmente.

---

## 1. Rodar tudo junto (modo normal)

```bash
# Build + start
docker compose up --build

# Em background
docker compose up --build -d

# Parar
docker compose down
```

Serviços disponíveis:

| Serviço | URL |
|---|---|
| Flask (direto) | http://localhost:5000 |
| Proxy OpenResty | http://localhost:8081 |

---

## 2. Rodar cada container separadamente (docker run manual)

### 2.1 Criar a rede compartilhada

```bash
docker network create proxy-net
```

### 2.2 Build das imagens

```bash
# Flask
docker build --network=host -t flask-service ./service

# OpenResty
docker build --network=host -t proxy-open-resty ./open-resty
```

### 2.3 Subir o Flask

```bash
docker run -d \
  --name flask-service \
  --network proxy-net \
  -e PORT=5000 \
  -p 5000:5000 \
  flask-service
```

### 2.4 Subir o proxy

```bash
docker run -d \
  --name proxy \
  --network proxy-net \
  -e UPSTREAM_HOST=flask-service \
  -e UPSTREAM_PORT=5000 \
  -p 8081:8081 \
  proxy-open-resty
```

### 2.5 Parar e remover

```bash
docker stop flask-service proxy
docker rm flask-service proxy
docker network rm proxy-net
```

---

## 3. Rodar o Flask manualmente (sem Docker)

```bash
cd service

# Criar venv (primeira vez)
python -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt

# Rodar
PORT=5000 python app.py
```

---

## 4. Modo debug — PyCharm + proxy no Docker

Neste modo, o Flask roda no host (com breakpoints no PyCharm) e apenas o OpenResty fica em container.

### 4.1 No PyCharm

Crie uma Run/Debug Configuration apontando para `service/app.py` e certifique-se de que a variável de ambiente `PORT=5000` está configurada.

### 4.2 Subir só o proxy

```bash
docker compose -f docker-compose.host-debug.yml up --build
```

O proxy usa `network_mode: host`, então ele acessa `localhost:5000` diretamente.

Acesse via proxy em: http://localhost:8081

### 4.3 Parar

```bash
docker compose -f docker-compose.host-debug.yml down
```

---

## 5. Testar os endpoints manualmente

### Via curl

```bash
# Diretamente no Flask
curl http://localhost:5000/health
curl http://localhost:5000/route-a
curl http://localhost:5000/route-b

# Via proxy OpenResty (com headers de proxy na resposta)
curl -v http://localhost:8081/health
curl http://localhost:8081/route-a
curl http://localhost:8081/route-b
```

Para inspecionar o header `x-proxy-flavour` adicionado pelo proxy:

```bash
curl -sI http://localhost:8081/health
```

Resposta esperada contendo:
```
x-proxy-flavour: openresty
Server: openresty/1.x.x.x
```

---

## 6. Ver logs dos containers

### Logs em tempo real (follow)

```bash
# Ambos ao mesmo tempo
docker compose logs -f

# Só o Flask
docker compose logs -f service

# Só o proxy
docker compose logs -f proxy-open-resty
```

### Logs de containers iniciados manualmente

```bash
docker logs flask-service
docker logs proxy
docker logs -f proxy          # follow
docker logs --tail 50 proxy   # últimas 50 linhas
```

### O que esperar nos logs

**Flask** — uma linha de INFO por requisição recebida:
```
2026-07-05 17:51:56,897 INFO [service] health check from 172.x.x.x
172.x.x.x - - [05/Jul/2026 17:51:56] "GET /health HTTP/1.1" 200 -
```

**OpenResty (access_log no stdout)**:
```
172.x.x.x - - [05/Jul/2026:17:51:56 +0000] "GET /health HTTP/1.1" 200 16 "-" "curl/8.21.0"
```

> O IP que aparece nos logs do Flask será o IP do container do proxy (não do cliente original), pois o header `x-real-ip` é passado mas o Flask loga `request.remote_addr`.

---

## 7. Script de validação automatizada

```bash
# Validar via proxy (padrão)
python test_proxy.py

# Validar direto no Flask (sem proxy, para comparar)
python test_proxy.py --direct

# Usar host/porta customizados
python test_proxy.py --host localhost --port 8081
```

O script faz 10 requisições cobrindo todos os endpoints, exibe status HTTP, latência, o header `x-proxy-flavour` e o body de cada rota.

---

## 8. Variáveis de ambiente

| Variável | Serviço | Padrão | Descrição |
|---|---|---|---|
| `PORT` | Flask | `5000` | Porta em que o Flask escuta |
| `UPSTREAM_HOST` | OpenResty | `service` | Hostname do backend |
| `UPSTREAM_PORT` | OpenResty | `5000` | Porta do backend |

---

## 9. Endpoints do Flask

| Método | Path | Descrição |
|---|---|---|
| `GET` | `/health` | Health check — retorna `{"status": "ok"}` |
| `GET` | `/route-a` | Rota A — retorna `{"route": "a", "message": "hi from route a"}` |
| `GET` | `/route-b` | Rota B — retorna `{"route": "b", "message": "hi from route b"}` |
