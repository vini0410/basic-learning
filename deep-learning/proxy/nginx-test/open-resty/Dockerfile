FROM openresty/openresty:alpine-fat

# Instala a biblioteca do prometheus para Lua usando OPM
RUN opm get knyar/nginx-lua-prometheus

# Instala lua-resty-http para envio de métricas via OTLP HTTP
RUN opm get ledgetech/lua-resty-http

COPY nginx.conf.template /etc/nginx/templates/nginx.conf.template
COPY otlp_exporter.lua /usr/local/openresty/lualib/otlp_exporter.lua
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENV UPSTREAM_HOST=service
ENV UPSTREAM_PORT=5000
ENV OTEL_ENDPOINT=http://otel-collector:4318

EXPOSE 8081

ENTRYPOINT ["/entrypoint.sh"]
