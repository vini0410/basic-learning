package com.example.pricing.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record PriceRequest(
    String serviceId,
    double lat,
    double lon,
    String userId
) {}
