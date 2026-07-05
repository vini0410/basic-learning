FROM openresty/openresty:alpine-fat

COPY nginx.conf.template /etc/nginx/templates/nginx.conf.template
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENV UPSTREAM_HOST=service
ENV UPSTREAM_PORT=5000

EXPOSE 8081

ENTRYPOINT ["/entrypoint.sh"]
