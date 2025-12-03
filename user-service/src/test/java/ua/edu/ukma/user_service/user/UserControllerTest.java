package ua.edu.ukma.user_service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ua.edu.ukma.user_service.config.SecurityConfig;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
        "internal.api.key=test-secret",
        "grpc.server.port=-1"
})
@DisplayName("UserController Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto createSampleDto() {
        return new UserDto(
                1L,
                UserRole.USER,
                "testuser",
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                "+380501234567",
                LocalDate.of(1990, 1, 1)
        );
    }

    @Test
    @DisplayName("POST /api/users -> creates user and returns 201")
    void create_returnsCreated() throws Exception {
        UserDto inputDto = createSampleDto();
        inputDto.setId(null);
        UserDto createdDto = createSampleDto();

        when(userService.create(any(UserDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto))
                        .header("x-api-key", "test-secret"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/users/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.firstName", is("John")));
    }

    @Test
    @DisplayName("GET /api/users/{id} -> returns user DTO")
    void getById_returnsUserDto() throws Exception {
        UserDto dto = createSampleDto();
        when(userService.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/users/1")
                        .header("x-api-key", "test-secret"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("testuser")))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.lastName", is("Doe")));
    }

    @Test
    @DisplayName("GET /api/users -> returns list of users")
    void getAll_returnsListOfUsers() throws Exception {
        UserDto user1 = createSampleDto();
        UserDto user2 = createSampleDto();
        user2.setId(2L);
        user2.setUsername("user2");

        when(userService.getAll()).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users")
                        .header("x-api-key", "test-secret"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("testuser")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("user2")));
    }

    @Test
    @DisplayName("PUT /api/users/{id} -> updates user and returns updated DTO")
    void update_returnsUpdatedDto() throws Exception {
        UserDto updateDto = createSampleDto();
        updateDto.setFirstName("Jane");
        UserDto updatedDto = createSampleDto();
        updatedDto.setFirstName("Jane");

        when(userService.update(eq(1L), any(UserDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .header("x-api-key", "test-secret"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Jane")));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} -> returns 204 No Content")
    void delete_returnsNoContent() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1")
                        .header("x-api-key", "test-secret"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("POST /api/users/authenticate -> returns 404 Not Found")
    void authenticate_returnsNotFound() throws Exception {
        mockMvc.perform(post("/api/users/authenticate")
                        .param("username", "testuser")
                        .param("password", "password123")
                        .header("x-api-key", "test-secret"))
                .andExpect(status().isNotFound());
    }
}

