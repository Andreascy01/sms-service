package com.example.sms;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class MessageResourceTest {

    @Test
    public void testCreateMessageSuccessfully() {
        given()
                .contentType("application/json")
                .body("{\"sourceNumber\":\"+12025550123\",\"destinationNumber\":\"+447700900123\",\"content\":\"Hello test\"}")
                .when().post("/messages")
                .then()
                .statusCode(202)
                .body("status", is("PENDING"))
                .body("sourceNumber", is("+12025550123"));
    }

    @Test
    public void testValidationFailsForEmptyContent() {
        given()
                .contentType("application/json")
                .body("{\"sourceNumber\":\"+12025550123\",\"destinationNumber\":\"+447700900123\",\"content\":\"\"}")
                .when().post("/messages")
                .then()
                .statusCode(400)
                .body("[0].field", is("content"))
                .body("[0].message", containsString("cannot be empty"));
    }

    @Test
    public void testValidationFailsForInvalidPhoneNumber() {
        given()
                .contentType("application/json")
                .body("{\"sourceNumber\":\"0123\",\"destinationNumber\":\"+447700900123\",\"content\":\"Hi\"}")
                .when().post("/messages")
                .then()
                .statusCode(400)
                .body("[0].field", is("sourceNumber"))
                .body("[0].message", containsString("Invalid source phone number format"));
    }

    @Test
    public void testGetAllMessages() {
        given()
                .when().get("/messages")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0)); // could be empty or not
    }

    @Test
    public void testGetMessageNotFound() {
        given()
                .when().get("/messages/00000000-0000-0000-0000-000000000000")
                .then()
                .statusCode(404);
    }
}
