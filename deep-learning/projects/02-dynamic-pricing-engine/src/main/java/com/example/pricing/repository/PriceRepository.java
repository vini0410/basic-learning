package com.example.pricing.repository;

import com.datastax.oss.quarkus.runtime.api.session.QuarkusCqlSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PriceRepository {

    @Inject
    QuarkusCqlSession session;

    public Uni<Double> getBasePrice(String serviceId) {
        String query = "SELECT base_price FROM base_prices WHERE service_id = ?";
        
        return Uni.createFrom().completionStage(
                session.executeAsync(query, serviceId)
            )
            .map(rs -> {
                var row = rs.one();
                return (row != null) ? row.getDouble("base_price") : 50.0; // Default se não encontrar
            });
    }
}
