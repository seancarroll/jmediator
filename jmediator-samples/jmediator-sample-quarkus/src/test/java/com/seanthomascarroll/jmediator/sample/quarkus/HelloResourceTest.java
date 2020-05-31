package com.seanthomascarroll.jmediator.sample.quarkus;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class HelloResourceTest {

    @Test
    void testHelloEndpoint() {
        Map<String, Object> jsonAsMap = new HashMap<>();
        jsonAsMap.put("name", "Sean");

        given().contentType("application/json")
            .body(jsonAsMap)
            .when().post("/hello")
            .then()
            .statusCode(200)
            .body(is("Hello Sean"));
    }

}
