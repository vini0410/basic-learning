package com.example.pricing;

import com.example.pricing.model.PriceRequest;
import com.example.pricing.model.PriceResponse;
import com.example.pricing.service.PriceService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;

@QuarkusTest
public class PriceResourceTest {

    @InjectMock
    PriceService priceService;

    @Test
    public void testGetPriceSuccess() {
        PriceRequest request = new PriceRequest("service-1", 10.0, 20.0, "user-1");
        PriceResponse response = new PriceResponse("service-1", 100.0, 120.0, 1.2, Collections.emptyList());

        Mockito.when(priceService.calculatePrice(Mockito.any())).thenReturn(Uni.createFrom().item(response));

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/price")
            .then()
            .statusCode(200)
            .body("finalPrice", is(120.0f))
            .body("multiplier", is(1.2f));
    }

    @Test
    public void testGetPricePartialContentFallback() {
        PriceRequest request = new PriceRequest("service-1", 10.0, 20.0, "user-1");
        // Simula resposta com avisos (fallback acionado no serviço)
        PriceResponse response = new PriceResponse("service-1", 100.0, 100.0, 1.0, List.of("Surge pricing logic failed"));

        Mockito.when(priceService.calculatePrice(Mockito.any())).thenReturn(Uni.createFrom().item(response));

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/price")
            .then()
            .statusCode(206) // Partial Content
            .body("warnings", hasItem("Surge pricing logic failed"));
    }

    @Test
    public void testInvalidCoordinates() {
        PriceRequest request = new PriceRequest("service-1", 100.0, 200.0, "user-1");

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/price")
            .then()
            .statusCode(400)
            .body("error", is("Invalid coordinates"));
    }

    @Test
    public void testGlobalTimeout() {
        PriceRequest request = new PriceRequest("service-1", 10.0, 20.0, "user-1");

        // Simula um delay maior que o Timeout global de 500ms definido no Resource
        Mockito.when(priceService.calculatePrice(Mockito.any()))
                .thenReturn(Uni.createFrom().item(new PriceResponse("id", 1, 1, 1, List.of()))
                .onItem().delayIt().by(Duration.ofMillis(600)));

        given()
            .contentType(ContentType.JSON)
            .body(request)
            .when().post("/price")
            .then()
            .statusCode(504); // Gateway Timeout
    }
}
