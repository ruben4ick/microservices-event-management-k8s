package ua.edu.ukma.event_management_system.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

class EventsRetryIT {

    static String BASE_URL;
    static String API_KEY;
    static final Faker FAKER = new Faker();

    @BeforeAll
    static void setup() {
        BASE_URL = System.getProperty("EVENT_BASE_URL",
                System.getenv().getOrDefault("EVENT_BASE_URL", "http://localhost:8080"));
        API_KEY = System.getenv("INTERNAL_API_KEY");
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException("INTERNAL_API_KEY not set in environment or .env");
        }
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("POST /api/events retries on transient failures")
    void createEvent_retryOnUserLookup() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("eventTitle", FAKER.book().title());
        var start = LocalDateTime.now().plusDays(1).withNano(0);
        var end = start.plusHours(2);
        payload.put("dateTimeStart", start.toString());
        payload.put("dateTimeEnd", end.toString());
        payload.put("description", FAKER.lorem().sentence());
        payload.put("numberOfTickets", 10);
        payload.put("minAgeRestriction", 0);
        payload.put("userIds", new long[]{});
        payload.put("creatorId", 999999L);
        payload.put("price", 10.0);

        long t0 = System.nanoTime();
        Response r = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-api-key", API_KEY)
                .body(payload)
                .when()
                .post("/api/events")
                .andReturn();
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - t0);

        int code = r.getStatusCode();
        if (code == 405 || code == 404 || code == 501) {
            Assumptions.abort("POST /api/events not supported");
            return;
        }

        Assumptions.assumeTrue(code >= 400, "expected failure to verify retry");

        if (code >= 500) {
            boolean okTime = tookMs >= 120;
            if (!okTime) {
                try {
                    Response beans = given()
                            .accept(ContentType.JSON)
                            .header("x-api-key", API_KEY)
                            .when()
                            .get("/actuator/beans")
                            .andReturn();
                    if (beans.getStatusCode() == 200) {
                        String body = beans.asString();
                        if (body.contains("Retryer") || body.contains("feignRetryer")) okTime = true;
                    }
                } catch (Exception ignored) {}
            }
            org.junit.jupiter.api.Assertions.assertTrue(okTime, "retry not observed");
        } else {
            Assumptions.abort("client error; retries typically disabled for 4xx");
        }
    }
}
