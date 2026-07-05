#! /bin/sh
set -eu

: "${UPSTREAM_HOST=service}"
: "${UPSTREAM_PORT=5000}"

export UPSTREAM_HOST
export UPSTREAM_PORT

sed \
  -e "s|\${UPSTREAM_HOST}|${UPSTREAM_HOST}|g" \
  -e "s|\${UPSTREAM_PORT}|${UPSTREAM_PORT}|g" \
  /etc/nginx/templates/nginx.conf.template \
  > /usr/local/openresty/nginx/conf/nginx.conf

exec openresty -g 'daemon off;'