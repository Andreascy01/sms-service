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

    @Test
    public void testGetMessagesByStatus() {
        // Create a message with status SENT
        given()
                .contentType("application/json")
                .body("{\"sourceNumber\":\"+12025550124\",\"destinationNumber\":\"+447700900124\",\"content\":\"Status test\"}")
                .when().post("/messages")
                .then().statusCode(202);

        // Simulate delivery to change status (assuming service updates status)
        // You may need to fetch the ID from the response in a real test

        // Filter by status PENDING
        given()
                .queryParam("status", "PENDING")
                .when().get("/messages")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    public void testGetMessagesBySourceNumber() {
        given()
                .contentType("application/json")
                .body("{\"sourceNumber\":\"+12025550125\",\"destinationNumber\":\"+447700900125\",\"content\":\"Source test\"}")
                .when().post("/messages")
                .then().statusCode(202);

        given()
                .queryParam("sourceNumber", "+12025550125")
                .when().get("/messages")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].sourceNumber", is("+12025550125"));
    }

    @Test
    public void testGetMessagesByDestinationNumber() {
        given()
                .contentType("application/json")
                .body("{\"sourceNumber\":\"+12025550126\",\"destinationNumber\":\"+447700900126\",\"content\":\"Destination test\"}")
                .when().post("/messages")
                .then().statusCode(202);

        given()
                .queryParam("destinationNumber", "+447700900126")
                .when().get("/messages")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].destinationNumber", is("+447700900126"));
    }

    @Test
    public void testSimulateMessageDeliverySuccess() {
        // Create a message and get its ID
        String id = given()
                .contentType("application/json")
                .body("{\"sourceNumber\":\"+12025550127\",\"destinationNumber\":\"+447700900127\",\"content\":\"Simulate test\"}")
                .when().post("/messages")
                .then().statusCode(202)
                .extract().path("id");

        given()
                .when().put("/messages/" + id + "/simulate")
                .then()
                .statusCode(200)
                .body("id", is(id));
    }

    @Test
    public void testSimulateMessageDeliveryNotFound() {
        given()
                .when().put("/messages/00000000-0000-0000-0000-000000000000/simulate")
                .then()
                .statusCode(404)
                .body("Error", is("Message not found"));
    }
}
