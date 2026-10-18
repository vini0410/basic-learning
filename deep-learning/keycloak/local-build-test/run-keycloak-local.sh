#!/usr/bin/env bash

# ------------------------------------------------------------
# Run a local Keycloak instance (no Docker) while using the
# PostgreSQL container started by docker-compose as its database.
# ------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# Ensure the Docker DB container is running
if ! docker ps --filter "name=keycloak-db" --format "{{.Names}}" | grep -q "keycloak-db"; then
  echo "[INFO] PostgreSQL container not running – starting via docker compose..."
  docker compose -f "$ROOT_DIR/docker/docker-compose.yml" up -d postgres
fi

# Wait for DB to become healthy (simple loop)
echo "[INFO] Waiting for PostgreSQL to be healthy..."
for i in {1..30}; do
  status=$(docker inspect -f "{{.State.Health.Status}}" keycloak-db 2>/dev/null || echo "none")
  if [[ "$status" == "healthy" ]]; then break; fi
  sleep 2
done
if [[ "$status" != "healthy" ]]; then
  echo "[ERROR] PostgreSQL did not become healthy in time. Exiting."
  exit 1
fi

# Directory for Keycloak distribution
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
KC_DIR="$SCRIPT_DIR/keycloak-dist"
KC_VERSION="24.0.5"
KC_URL="https://github.com/keycloak/keycloak/releases/download/${KC_VERSION}/keycloak-${KC_VERSION}.zip"

# Download Keycloak if not present
if [[ ! -d "$KC_DIR" ]]; then
  echo "[INFO] Downloading Keycloak ${KC_VERSION}..."
  curl -L -o "$SCRIPT_DIR/keycloak-${KC_VERSION}.zip" "$KC_URL"
  unzip -q "$SCRIPT_DIR/keycloak-${KC_VERSION}.zip" -d "$SCRIPT_DIR"
  mv "$SCRIPT_DIR/keycloak-${KC_VERSION}" "$KC_DIR"
  rm "$SCRIPT_DIR/keycloak-${KC_VERSION}.zip"
fi

# Export DB connection environment variables for dev mode
export KC_DB=postgres
export KC_DB_URL_HOST=localhost   # Docker forwards to host network
export KC_DB_URL_DATABASE=keycloak
export KC_DB_USERNAME=keycloak
export KC_DB_PASSWORD=password

# Optional admin credentials for first start (dev mode creates if absent)
export KEYCLOAK_ADMIN=admin
export KEYCLOAK_ADMIN_PASSWORD=admin

# Build and copy custom provider jar if keycloak-custom exists
if [[ -d "$ROOT_DIR/keycloak-custom" ]]; then
  echo "[INFO] Building keycloak-custom..."
  "$ROOT_DIR/gradlew" -p "$ROOT_DIR" :keycloak-custom:build -q
  mkdir -p "$KC_DIR/providers"
  cp "$ROOT_DIR/keycloak-custom/build/libs/"*.jar "$KC_DIR/providers/"
  echo "[INFO] Custom provider jar deployed to $KC_DIR/providers"
fi

# Start Keycloak in development mode (pass any additional CLI args like --debug)
cd "$KC_DIR"
./bin/kc.sh start-dev --log-level=debug "$@"
