package com.custom.keycloak.resource;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

public class CustomRealmResourceProviderFactory implements RealmResourceProviderFactory {

    public static final String ID = "custom";

    @Override
    public RealmResourceProvider create(KeycloakSession session) {
        return new CustomRealmResourceProvider(session);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void init(Config.Scope config) {
        // Inicialização com configurações do standalone.xml / keycloak.conf se necessário
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
        // Pós-inicialização do factory
    }

    @Override
    public void close() {
        // Limpeza de recursos
    }
}
