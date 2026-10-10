package com.example.provider;

import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.Provider;
import org.keycloak.provider.ProviderFactory;

/**
 * Simple example provider that can be referenced via SPI.
 */
public class HelloProvider implements Provider {
    private final KeycloakSession session;

    public HelloProvider(KeycloakSession session) {
        this.session = session;
    }

    public String greet() {
        return "Hello from custom provider!";
    }

    @Override
    public void close() {
        // nothing to clean up
    }
}

class HelloProviderFactory implements ProviderFactory<HelloProvider> {
    @Override
    public HelloProvider create(KeycloakSession session) {
        return new HelloProvider(session);
    }

    @Override
    public void init(org.keycloak.Config.Scope config) { }

    @Override
    public void postInit(org.keycloak.models.KeycloakSessionFactory factory) { }

    @Override
    public void close() { }

    @Override
    public String getId() {
        return "hello-provider";
    }
}
