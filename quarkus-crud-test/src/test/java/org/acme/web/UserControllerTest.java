package org.acme.web;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.model.Address;
import org.acme.model.User;
import org.acme.service.UserService;
import org.acme.web.dto.AddressDTO;
import org.acme.web.dto.UserRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@QuarkusTest
public class UserControllerTest {

    @InjectMock
    UserService userService;

    private User user;
    private UserRequestDTO userRequest;

    @BeforeEach
    public void setup() {
        // Mocked User Model
        user = new User();
        user.setId("65f0e15b5a7b6e3a3e6b7b1e"); // Example ObjectId
        user.setName("John");
        user.setSurname("Doe");
        user.setEmail("john.doe@example.com");
        user.setNickname("johndoe");
        user.setPassword("password123");

        Address address = new Address();
        address.setCode("12345-678");
        address.setStreet("Main Street");
        address.setNumber("123");
        address.setComplement("Apt 4B");
        user.setAddresses(Collections.singletonList(address));

        // User Request DTO for tests
        userRequest = new UserRequestDTO();
        userRequest.setName("John");
        userRequest.setSurname("Doe");
        userRequest.setEmail("john.doe@example.com");
        userRequest.setNickname("johndoe");
        userRequest.setPassword("password123");

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCode("12345-678");
        addressDTO.setStreet("Main Street");
        addressDTO.setNumber("123");
        addressDTO.setComplement("Apt 4B");
        userRequest.setAddresses(Collections.singletonList(addressDTO));
    }

    @Test
    public void testCreateUser() {
        when(userService.createUser(any(User.class))).thenReturn(user);

        given()
                .contentType(ContentType.JSON)
                .body(userRequest)
                .when()
                .post("/users")
                .then()
                .statusCode(200)
                .body("id", is("65f0e15b5a7b6e3a3e6b7b1e"))
                .body("name", is("John"))
                .body("email", is("john.doe@example.com"));
    }

    @Test
    public void testGetUsers() {
        when(userService.listAllUsers()).thenReturn(Collections.singletonList(user));

        given()
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", is("65f0e15b5a7b6e3a3e6b7b1e"))
                .body("[0].name", is("John"));
    }

    @Test
    public void testGetUserById() {
        when(userService.getUserById(user.getId())).thenReturn(user);

        given()
                .when().get("/users/{id}", user.getId())
                .then()
                .statusCode(200)
                .body("id", is(user.getId()))
                .body("name", is("John"))
                .body("addresses", hasSize(1))
                .body("addresses[0].street", is("Main Street"));
    }

    @Test
    public void testGetUserAddresses() {
        when(userService.getUserById(user.getId())).thenReturn(user);

        given()
                .when().get("/users/{id}/addresses", user.getId())
                .then()
                .statusCode(200)
                .body("id", is(user.getId()))
                .body("addresses", hasSize(1))
                .body("addresses[0].street", is("Main Street"));
    }

    @Test
    public void testUpdateUser() {
        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setName("John Updated");
        updatedUser.setAddresses(user.getAddresses()); // Keep addresses

        when(userService.updateUser(any(String.class), any(User.class))).thenReturn(updatedUser);

        userRequest.setName("John Updated"); // Update request DTO

        given()
                .contentType(ContentType.JSON)
                .body(userRequest)
                .when().put("/users/{id}", user.getId())
                .then()
                .statusCode(200)
                .body("id", is(user.getId()))
                .body("name", is("John Updated"))
                .body("addresses", hasSize(1));
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(userService).deleteUser(user.getId());

        given()
                .when().delete("/users/{id}", user.getId())
                .then()
                .statusCode(200)
                .body(is("usuario " + user.getId() + " deletado"));

        // Optionally, you can verify that a subsequent GET returns 404
        when(userService.getUserById(user.getId())).thenThrow(new jakarta.ws.rs.NotFoundException());

        given()
            .when().get("/users/{id}", user.getId())
            .then()
            .statusCode(404);
    }
}