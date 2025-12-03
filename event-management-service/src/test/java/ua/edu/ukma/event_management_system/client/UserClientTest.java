package ua.edu.ukma.event_management_system.client;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserClient (Feign) Tests")
class UserClientTest {

    @Mock
    private UserClient userClient;

    @Test
    @DisplayName("getById: successful retrieval returns user DTO")
    void getById_success_returnsUserDto() {
        Long userId = 1L;
        UserDto expectedUser = new UserDto();
        expectedUser.setId(userId);
        expectedUser.setUsername("testuser");
        expectedUser.setFirstName("John");
        expectedUser.setLastName("Doe");
        expectedUser.setEmail("john.doe@example.com");

        when(userClient.getById(userId)).thenReturn(expectedUser);

        UserDto result = userClient.getById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());

        verify(userClient, times(1)).getById(userId);
    }

    @Test
    @DisplayName("getById: user not found throws FeignException.NotFound")
    void getById_notFound_throwsFeignExceptionNotFound() {
        Long userId = 999L;
        FeignException.NotFound notFoundException = createNotFoundException();

        when(userClient.getById(userId)).thenThrow(notFoundException);

        FeignException.NotFound exception = assertThrows(
                FeignException.NotFound.class,
                () -> userClient.getById(userId)
        );

        assertEquals(404, exception.status());
        verify(userClient, times(1)).getById(userId);
    }

    @Test
    @DisplayName("getAll: successful retrieval returns list of users")
    void getAll_success_returnsListOfUsers() {
        UserDto user1 = new UserDto();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setFirstName("Alice");
        user1.setLastName("Smith");
        user1.setEmail("alice@example.com");

        UserDto user2 = new UserDto();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setFirstName("Bob");
        user2.setLastName("Johnson");
        user2.setEmail("bob@example.com");

        List<UserDto> expectedUsers = List.of(user1, user2);

        when(userClient.getAll()).thenReturn(expectedUsers);

        List<UserDto> result = userClient.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());

        verify(userClient, times(1)).getAll();
    }

    @Test
    @DisplayName("getAll: empty list returns empty list")
    void getAll_emptyList_returnsEmptyList() {
        when(userClient.getAll()).thenReturn(Collections.emptyList());

        List<UserDto> result = userClient.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userClient, times(1)).getAll();
    }

    @Test
    @DisplayName("create: successful creation returns created user DTO")
    void create_success_returnsCreatedUserDto() {
        // Given
        UserDto inputDto = new UserDto();
        inputDto.setUsername("newuser");
        inputDto.setFirstName("New");
        inputDto.setLastName("User");
        inputDto.setEmail("newuser@example.com");

        UserDto createdDto = new UserDto();
        createdDto.setId(3L);
        createdDto.setUsername("newuser");
        createdDto.setFirstName("New");
        createdDto.setLastName("User");
        createdDto.setEmail("newuser@example.com");

        when(userClient.create(any(UserDto.class))).thenReturn(createdDto);

        UserDto result = userClient.create(inputDto);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("newuser", result.getUsername());
        assertEquals("New", result.getFirstName());
        assertEquals("User", result.getLastName());
        assertEquals("newuser@example.com", result.getEmail());

        verify(userClient, times(1)).create(inputDto);
    }

    @Test
    @DisplayName("create: validation error throws FeignException.BadRequest")
    void create_validationError_throwsFeignExceptionBadRequest() {
        UserDto invalidDto = new UserDto();

        FeignException.BadRequest badRequestException = createBadRequestException();

        when(userClient.create(any(UserDto.class))).thenThrow(badRequestException);

        FeignException.BadRequest exception = assertThrows(
                FeignException.BadRequest.class,
                () -> userClient.create(invalidDto)
        );

        assertEquals(400, exception.status());
        verify(userClient, times(1)).create(invalidDto);
    }

    private FeignException.NotFound createNotFoundException() {
        Request req = Request.create(
                Request.HttpMethod.GET,
                "http://user-service/api/users/999",
                Collections.emptyMap(),
                null,
                new feign.RequestTemplate()
        );

        Response resp = Response.builder()
                .request(req)
                .status(404)
                .reason("Not Found")
                .headers(Collections.emptyMap())
                .build();

        return (FeignException.NotFound) FeignException.errorStatus("UserClient#getById", resp);
    }

    private FeignException.BadRequest createBadRequestException() {
        Request req = Request.create(
                Request.HttpMethod.POST,
                "http://user-service/api/users",
                Collections.emptyMap(),
                null,
                new feign.RequestTemplate()
        );

        Response resp = Response.builder()
                .request(req)
                .status(400)
                .reason("Bad Request")
                .headers(Collections.emptyMap())
                .build();

        return (FeignException.BadRequest) FeignException.errorStatus("UserClient#create", resp);
    }
}

