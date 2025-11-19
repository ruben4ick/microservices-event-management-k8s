//package ua.edu.ukma.event_management_system.api;
//
//import io.restassured.RestAssured;
//import io.restassured.http.ContentType;
//import io.restassured.response.Response;
//import net.datafaker.Faker;
//import org.junit.jupiter.api.Assumptions;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//import static io.restassured.RestAssured.given;
//import static org.hamcrest.Matchers.*;
//
//class EventsApiIT {
//
//    static String BASE_URL;
//    static String API_KEY;
//    static final Faker FAKER = new Faker();
//
//    @BeforeAll
//    static void setup() {
//        BASE_URL = System.getProperty("EVENT_BASE_URL",
//                System.getenv().getOrDefault("EVENT_BASE_URL", "http://localhost:8080"));
//        API_KEY = System.getenv("INTERNAL_API_KEY");
//        if (API_KEY == null || API_KEY.isBlank()) {
//            throw new IllegalStateException("INTERNAL_API_KEY not set in environment or .env");
//        }
//        RestAssured.baseURI = BASE_URL;
//    }
//
//    @Test
//    @DisplayName("GET /api/events as JSON")
//    void getEvents_json() {
//        given()
//                .header("x-api-key", API_KEY)
//                .accept(ContentType.JSON)
//                .when()
//                .get("/api/events")
//                .then()
//                .statusCode(anyOf(is(200), is(204)))
//                .contentType(startsWith("application/json"));
//    }
//
//    @Test
//    @DisplayName("GET /api/events/{id}/html")
//    void getEvent_html() {
//        Response list = given().header("x-api-key", API_KEY).accept(ContentType.JSON).when().get("/api/events")
//                .then().statusCode(anyOf(is(200), is(204))).extract().response();
//
//        Long id = extractId(list);
//        if (id == null) {
//            Long created = createEventOrNull();
//            Assumptions.assumeTrue(created != null, "cannot obtain event id");
//            id = created;
//        }
//
//        given()
//                .header("x-api-key", API_KEY)
//                .accept("text/html")
//                .when()
//                .get("/api/events/{id}/html", id)
//                .then()
//                .statusCode(200)
//                .contentType(startsWith("text/html"))
//                .body(anyOf(containsString("<html"), containsString("<!doctype")));
//    }
//
//    static Long extractId(Response r) {
//        try {
//            Object a = r.jsonPath().get("content[0].id");
//            if (a == null) a = r.jsonPath().get("[0].id");
//            if (a == null) return null;
//            if (a instanceof Number) return ((Number) a).longValue();
//            return Long.parseLong(String.valueOf(a));
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    static Long createEventOrNull() {
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("eventTitle", FAKER.book().title());
//        var start = LocalDateTime.now().plusDays(1).withNano(0);
//        var end = start.plusHours(2);
//        payload.put("dateTimeStart", start.toString());
//        payload.put("dateTimeEnd", end.toString());
//        payload.put("description", FAKER.lorem().sentence());
//        payload.put("numberOfTickets", 10);
//        payload.put("minAgeRestriction", 0);
//        payload.put("userIds", new long[]{});
//        payload.put("creatorId", 1L);
//        payload.put("price", 10.0);
//
//        Response resp = given().header("x-api-key", API_KEY).contentType(ContentType.JSON).accept(ContentType.JSON).body(payload)
//                .when().post("/api/events").andReturn();
//
//        if (resp.getStatusCode() == 201 || resp.getStatusCode() == 200) {
//            Object v = resp.jsonPath().get("id");
//            if (v == null) {
//                Response list = given().header("x-api-key", API_KEY).accept(ContentType.JSON).when().get("/api/events").andReturn();
//                return extractId(list);
//            }
//            if (v instanceof Number) return ((Number) v).longValue();
//            try { return Long.parseLong(String.valueOf(v)); } catch (Exception ignored) {}
//        }
//        return null;
//    }
//
//    @Test
//    @DisplayName("GET /api/events/export as CSV")
//    void getEvents_csv() {
//        Response r = given().header("x-api-key", API_KEY).accept("text/csv").when().get("/api/events/export")
//                .then().statusCode(anyOf(is(200), is(204))).extract().response();
//
//        if (r.getStatusCode() == 200) {
//            r.then()
//                    .header("Content-Disposition", containsString("events.csv"))
//                    .contentType(startsWith("text/csv"));
//        }
//    }
//}
