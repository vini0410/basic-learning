package com.example.pricing.service;

import com.example.pricing.model.PriceRequest;
import com.example.pricing.model.PriceResponse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
public class PriceServiceTest {

    @Inject
    PriceService priceService;

    @Test
    public void testCalculatePriceFlow() {
        PriceRequest request = new PriceRequest("service-test", 0.0, 0.0, "user-1");
        
        PriceResponse response = priceService.calculatePrice(request)
                .await().atMost(Duration.ofSeconds(2));

        assertNotNull(response);
        assertEquals("service-test", response.serviceId());
        assertTrue(response.basePrice() >= 50.0);
        
        // Como o serviço tem delay aleatório, pode ou não cair no fallback de 50ms
        if (response.multiplier() == 1.0) {
            assertFalse(response.warnings().isEmpty());
            assertTrue(response.warnings().get(0).contains("timed out"));
        } else {
            assertTrue(response.multiplier() >= 1.0);
            assertTrue(response.warnings().isEmpty());
        }
    }
}
