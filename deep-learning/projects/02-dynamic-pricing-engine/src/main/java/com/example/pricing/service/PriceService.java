package com.example.pricing.service;

import com.example.pricing.model.PriceRequest;
import com.example.pricing.model.PriceResponse;
import com.example.pricing.repository.PriceRepository;
import io.quarkus.cache.CacheResult;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class PriceService {

    private static final Logger LOG = LoggerFactory.getLogger(PriceService.class);
    @Inject
    PriceRepository priceRepository;

    @Inject
    GeofencingService geofencingService;

    @CacheResult(cacheName = "price-cache")
    public Uni<PriceResponse> calculatePrice(PriceRequest request) {
        LOG.info("Calculating price for service: {}", request.serviceId());

        return Uni.combine().all().unis(
                        priceRepository.getBasePrice(request.serviceId()),
                        geofencingService.getSurgeMultiplier(request.lat(), request.lon())
                                .onFailure().recoverWithItem(1.0)
                                .ifNoItem().after(Duration.ofMillis(50)).recoverWithItem(1.0)
                ).asTuple()
                .map(tuple -> {
                    double basePrice = tuple.getItem1();
                    double multiplier = tuple.getItem2();
                    double finalPrice = basePrice * multiplier;

                    List<String> warnings = new ArrayList<>();
                    if (multiplier == 1.0) {
                        warnings.add("Surge pricing logic failed or timed out. Using default multiplier.");
                    }

                    return new PriceResponse(
                            request.serviceId(),
                            basePrice,
                            finalPrice,
                            multiplier,
                            warnings
                    );
                });
    }
}
