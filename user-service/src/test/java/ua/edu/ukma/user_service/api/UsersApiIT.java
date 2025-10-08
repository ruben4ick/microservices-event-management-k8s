package ua.edu.ukma.user_service.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class UsersApiIT {

    static String BASE_URL;
    static String API_KEY;
    static final Faker FAKER = new Faker();

    @BeforeAll
    static void setup() {
        BASE_URL = System.getProperty("USER_BASE_URL",
                System.getenv().getOrDefault("USER_BASE_URL", "http://localhost:8081"));
        API_KEY = System.getenv("INTERNAL_API_KEY");
        if (API_KEY == null || API_KEY.isBlank()) {
            throw new IllegalStateException("INTERNAL_API_KEY is not set in environment or .env file");
        }
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("GET /api/users without API key is denied")
    void getUsers_deniedWithoutKey() {
        given()
                .accept(ContentType.JSON)
                .when()
                .get("/api/users")
                .then()
                .statusCode(anyOf(is(401), is(403)));
    }

    @Test
    @DisplayName("CRUD /api/users with API key")
    void crudUser_ok() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("userRole", "USER");
        payload.put("username", FAKER.internet().username());
        payload.put("firstName", FAKER.name().firstName());
        payload.put("lastName", FAKER.name().lastName());
        payload.put("email", FAKER.internet().emailAddress());
        payload.put("password", FAKER.internet().password(8, 16));
        payload.put("phoneNumber", "+1" + FAKER.number().digits(10));
        payload.put("dateOfBirth", LocalDate.now().minusYears(20).toString());

        Response create =
                given()
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .header("x-api-key", API_KEY)
                        .body(payload)
                        .when()
                        .post("/api/users")
                        .then()
                        .statusCode(anyOf(is(201), is(200)))
                        .extract().response();

        String location = create.getHeader("Location");
        long id;
        if (location != null && !location.isEmpty()) {
            id = Long.parseLong(location.replaceAll(".*/", ""));
        } else {
            id = create.jsonPath().getLong("id");
        }

        given()
                .accept(ContentType.JSON)
                .header("x-api-key", API_KEY)
                .when()
                .get("/api/users/{id}", id)
                .then()
                .statusCode(anyOf(is(200), is(204)))
                .body("id", anyOf(nullValue(), equalTo((int) id)));

        Map<String, Object> update = new HashMap<>(payload);
        update.put("firstName", FAKER.name().firstName());

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("x-api-key", API_KEY)
                .body(update)
                .when()
                .put("/api/users/{id}", id)
                .then()
                .statusCode(anyOf(is(200), is(204)));

        given()
                .header("x-api-key", API_KEY)
                .when()
                .delete("/api/users/{id}", id)
                .then()
                .statusCode(anyOf(is(200), is(204), is(202), is(204)));
    }
}
