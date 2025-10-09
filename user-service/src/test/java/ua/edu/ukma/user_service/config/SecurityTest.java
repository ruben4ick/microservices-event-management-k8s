package ua.edu.ukma.user_service.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ua.edu.ukma.user_service.user.UserDto;
import ua.edu.ukma.user_service.user.UserRole;
import ua.edu.ukma.user_service.user.UserService;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "internal.api.key=test-secret"
})
class SecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Test
    void whenNoApiKey_then401() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenWrongApiKey_then401() throws Exception {
        mockMvc.perform(get("/api/users").header("x-api-key", "wrong"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whenCorrectApiKey_then200AndBody() throws Exception {
        given(userService.getAll()).willReturn(List.of(
                new UserDto(
                        1L,
                        UserRole.ADMIN,
                        "alice",
                        "Alice",
                        "Doe",
                        "alice@example.com",
                        "password123",
                        "+380501234567",
                        LocalDate.of(2000, 1, 1)
                ),
                new UserDto(
                        2L,
                        UserRole.ADMIN,
                        "bob",
                        "Bob",
                        "Doe",
                        "bob@example.com",
                        "password123",
                        "+380501234567",
                        LocalDate.of(2000, 1, 1)
                )
        ));

        mockMvc.perform(get("/api/users")
                        .accept(APPLICATION_JSON)
                        .header("x-api-key", "test-secret"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L));
    }
}
