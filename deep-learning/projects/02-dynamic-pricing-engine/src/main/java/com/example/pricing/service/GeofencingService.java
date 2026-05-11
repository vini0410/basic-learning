package com.example.pricing.service;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.geo.GeoSearchArgs;
import io.quarkus.redis.datasource.geo.GeoUnit;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class GeofencingService {
    private static final Logger LOG = LoggerFactory.getLogger(GeofencingService.class);
    private static final String HOTSPOTS_KEY = "surge:hotspots";

    @Inject
    ReactiveRedisDataSource reactiveRedisDataSource;

    public Uni<Double> getSurgeMultiplier(double lat, double lon) {
        // No Redis, a ordem é Longitude, Latitude
        // O método correto no Quarkus 3.x é fromPosition
        return reactiveRedisDataSource.geo(String.class)
            .geosearch(HOTSPOTS_KEY, 
                new GeoSearchArgs<String>().fromCoordinate(lon, lat).byRadius(2, GeoUnit.KM))
            .map(hotspots -> {
                if (hotspots != null && !hotspots.isEmpty()) {
                    LOG.info("Hotspot detectado próximo a {}, {}. Aplicando Surge Pricing.", lat, lon);
                    return 1.5;
                }
                return 1.0;
            })
            .onFailure().recoverWithItem(th -> {

                LOG.error("Erro reativo no Redis: {}", th.getMessage());
                return 1.0;
            });
    }

    public Uni<Void> addHotspot(String name, double lat, double lon) {
        return reactiveRedisDataSource.geo(String.class)
            .geoadd(HOTSPOTS_KEY, lon, lat, name)
            .replaceWithVoid();
    }
}
