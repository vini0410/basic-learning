ARG KEYCLOAK_VERSION=24.0.5
FROM quay.io/keycloak/keycloak:${KEYCLOAK_VERSION} as builder

# Copia os provedores JAR customizados compilados pelo Gradle
COPY providers/ /opt/keycloak/providers/

# Executa otimização e registro das extensões
RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:${KEYCLOAK_VERSION}
COPY --from=builder /opt/keycloak/ /opt/keycloak/

ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
