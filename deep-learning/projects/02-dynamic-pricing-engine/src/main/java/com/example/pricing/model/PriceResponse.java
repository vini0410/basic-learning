package com.example.pricing.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;

@RegisterForReflection
public record PriceResponse(
    String serviceId,
    double basePrice,
    double finalPrice,
    double multiplier,
    List<String> warnings
) {}
