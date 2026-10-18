package com.custom.keycloak.resource;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

public class CustomRealmResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;

    public CustomRealmResourceProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public Object getResource() {
        return new CustomRealmResource(session);
    }

    @Override
    public void close() {
        // Nada a fechar por requisição
    }
}
