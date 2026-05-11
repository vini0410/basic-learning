package com.example.pricing.model;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class BasePrice {
    private String serviceId;
    private Double basePrice;
    private String description;

    public BasePrice() {}

    public BasePrice(String serviceId, Double basePrice, String description) {
        this.serviceId = serviceId;
        this.basePrice = basePrice;
        this.description = description;
    }

    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
