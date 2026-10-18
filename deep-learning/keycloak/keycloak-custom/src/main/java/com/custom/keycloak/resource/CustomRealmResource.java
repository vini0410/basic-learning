package com.custom.keycloak.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class CustomRealmResource {

    private final KeycloakSession session;

    public CustomRealmResource(KeycloakSession session) {
        this.session = session;
    }

    /**
     * Endpoint: GET /realms/{realm}/custom/health
     * Retorna o status de saúde do realm atual.
     */
    @GET
    @Path("health")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHealth() {
        RealmModel realm = session.getContext().getRealm();

        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", Instant.now().toString());
        health.put("realm", realm != null ? realm.getName() : "unknown");
        health.put("realmEnabled", realm != null && realm.isEnabled());

        return Response.ok(health).build();
    }

    /**
     * Endpoint: GET /realms/{realm}/custom/version
     * Retorna informações de versão da extensão e do Keycloak.
     */
    @GET
    @Path("version")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVersion() {
        Map<String, Object> version = new HashMap<>();
        version.put("extensionVersion", "1.0.0-SNAPSHOT");
        version.put("keycloakVersion", org.keycloak.common.Version.VERSION);
        version.put("javaVersion", System.getProperty("java.version"));
        version.put("buildTime", Instant.now().toString());

        return Response.ok(version).build();
    }
}
