package ua.edu.ukma.event_management_system.event;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import ua.edu.ukma.event_management_system.client.UserClient;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;


@SpringBootTest
class EventRetryTest {

    @Autowired
    private EventService eventService;

    @MockBean
    private UserClient userClient;

    private FeignException serviceUnavailable() {
        Request req = Request.create(
                Request.HttpMethod.GET,
                "http://user-service/api/users/1",
                Collections.emptyMap(),
                null,
                new feign.RequestTemplate()
        );

        var resp = Response.builder()
                .request(req)
                .status(503)
                .reason("Service Unavailable")
                .headers(Collections.emptyMap())
                .build();

        return FeignException.errorStatus("UserClient#getById", resp);
    }

    @Test
    @DisplayName("ensureUserExists: 2 failed retries then succeeds on 3rd attempt")
    void ensureUserExists_retriesThenSucceeds() {
        var ex = serviceUnavailable();

        Mockito.when(userClient.getById(anyLong()))
                .thenThrow(ex)
                .thenThrow(ex)
                .thenReturn(null);

        assertDoesNotThrow(() -> eventService.ensureUserExists(1L));

        Mockito.verify(userClient, times(3)).getById(1L);
    }

    @Test
    @DisplayName("@Recover: throws IllegalStateException('User-service unavailable') after 3 retires")
    void ensureUserExists_exhaustedRetries_callsRecoverAndThrowsIllegalState() {
        var ex = serviceUnavailable();

        Mockito.when(userClient.getById(anyLong()))
                .thenThrow(ex)
                .thenThrow(ex)
                .thenThrow(ex);

        var thrown = assertThrows(IllegalStateException.class,
                () -> eventService.ensureUserExists(777L));

        assertTrue(thrown.getMessage().contains("User-service unavailable"));

        Mockito.verify(userClient, times(3)).getById(777L);
    }
}
