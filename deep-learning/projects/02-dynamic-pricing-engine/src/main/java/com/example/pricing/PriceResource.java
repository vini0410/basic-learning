package com.example.pricing;

import com.example.pricing.model.PriceRequest;
import com.example.pricing.service.PriceService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Duration;

@Path("/price")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PriceResource {

    @Inject
    PriceService priceService;

    @jakarta.inject.Inject
    com.example.pricing.service.GeofencingService geofencingService;

    @POST
    @Path("/admin/hotspot")
    public Uni<Response> addHotspot(com.example.pricing.model.PriceRequest request) {
        return geofencingService.addHotspot(request.serviceId(), request.lat(), request.lon())
                .map(v -> Response.ok("Hotspot added").build());
    }

    @POST
    public Uni<Response> getPrice(PriceRequest request) {
        if (request.lat() < -90 || request.lat() > 90 || request.lon() < -180 || request.lon() > 180) {
            return Uni.createFrom().item(Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Invalid coordinates\"}")
                    .build());
        }

        return priceService.calculatePrice(request)
                .ifNoItem().after(Duration.ofMillis(500)).failWith(new RuntimeException("Global Timeout"))
                .map(priceResponse -> {
                    if (!priceResponse.warnings().isEmpty()) {
                        return Response.status(206) // Partial Content
                                .entity(priceResponse)
                                .build();
                    }
                    return Response.ok(priceResponse).build();
                })
                .onFailure().recoverWithItem(th -> {
                    if (th.getMessage().contains("Global Timeout")) {
                        return Response.status(Response.Status.GATEWAY_TIMEOUT).build();
                    }
                    return Response.serverError().build();
                });
    }
}
