package ua.edu.ukma.event_management_system.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ua.edu.ukma.event_management_system.client.UserDto;
import ua.edu.ukma.user_service.grpc.*;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserGrpcClient Tests")
class UserGrpcClientTest {

    @Mock
    private UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userStub;

    @Mock
    private UserGrpcServiceGrpc.UserGrpcServiceStub asyncStub;

    private UserGrpcClient userGrpcClient;

    @BeforeEach
    void setUp() throws Exception {
        userGrpcClient = new UserGrpcClient();
        
        Field userStubField = UserGrpcClient.class.getDeclaredField("userStub");
        userStubField.setAccessible(true);
        userStubField.set(userGrpcClient, userStub);
        
        Field asyncStubField = UserGrpcClient.class.getDeclaredField("asyncStub");
        asyncStubField.setAccessible(true);
        asyncStubField.set(userGrpcClient, asyncStub);
    }

    @Test
    @DisplayName("getUserById: successful retrieval returns user response")
    void getUserById_success_returnsUserResponse() {
        long userId = 1L;
        UserResponse expectedUser = UserResponse.newBuilder()
                .setId(userId)
                .setUsername("testuser")
                .setFirstName("John")
                .setLastName("Doe")
                .setEmail("john.doe@example.com")
                .build();

        GetUserByIdResponse response = GetUserByIdResponse.newBuilder()
                .setUser(expectedUser)
                .build();

        when(userStub.getUserById(any(GetUserByIdRequest.class))).thenReturn(response);

        UserResponse result = userGrpcClient.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());

        ArgumentCaptor<GetUserByIdRequest> requestCaptor = ArgumentCaptor.forClass(GetUserByIdRequest.class);
        verify(userStub, times(1)).getUserById(requestCaptor.capture());
        assertEquals(userId, requestCaptor.getValue().getId());
    }

    @Test
    @DisplayName("getUserById: NOT_FOUND error throws ResponseStatusException with 404")
    void getUserById_notFound_throwsResponseStatusException() {
        long userId = 999L;
        StatusRuntimeException notFoundException = Status.NOT_FOUND
                .withDescription("User not found")
                .asRuntimeException();

        when(userStub.getUserById(any(GetUserByIdRequest.class)))
                .thenThrow(notFoundException);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userGrpcClient.getUserById(userId)
        );

        assertEquals(404, exception.getStatusCode().value());
        assertTrue(exception.getMessage().contains("User with id=" + userId + " not found"));
        verify(userStub, times(1)).getUserById(any(GetUserByIdRequest.class));
    }

    @Test
    @DisplayName("getUserById: INTERNAL error throws ResponseStatusException with 500")
    void getUserById_internalError_throwsResponseStatusException() {
        long userId = 1L;
        StatusRuntimeException internalException = Status.INTERNAL
                .withDescription("Internal server error")
                .asRuntimeException();

        when(userStub.getUserById(any(GetUserByIdRequest.class)))
                .thenThrow(internalException);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userGrpcClient.getUserById(userId)
        );

        assertEquals(500, exception.getStatusCode().value());
        assertTrue(exception.getMessage().contains("gRPC error"));
        verify(userStub, times(1)).getUserById(any(GetUserByIdRequest.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("listUsers: successful retrieval returns list of users")
    void listUsers_success_returnsListOfUsers() {
        UserResponse user1 = UserResponse.newBuilder()
                .setId(1L)
                .setUsername("user1")
                .setFirstName("Alice")
                .setLastName("Smith")
                .setEmail("alice@example.com")
                .build();

        UserResponse user2 = UserResponse.newBuilder()
                .setId(2L)
                .setUsername("user2")
                .setFirstName("Bob")
                .setLastName("Johnson")
                .setEmail("bob@example.com")
                .build();

        doAnswer(invocation -> {
            StreamObserver<UserResponse> observer = invocation.getArgument(1);
            observer.onNext(user1);
            observer.onNext(user2);
            observer.onCompleted();
            return null;
        }).when(asyncStub).listUsers(any(ListUsersRequest.class), any(StreamObserver.class));

        List<UserDto> result = userGrpcClient.listUsers();

        assertNotNull(result);
        assertEquals(2, result.size());

        UserDto dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("user1", dto1.getUsername());
        assertEquals("Alice", dto1.getFirstName());
        assertEquals("Smith", dto1.getLastName());
        assertEquals("alice@example.com", dto1.getEmail());

        UserDto dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("user2", dto2.getUsername());
        assertEquals("Bob", dto2.getFirstName());
        assertEquals("Johnson", dto2.getLastName());
        assertEquals("bob@example.com", dto2.getEmail());

        verify(asyncStub, times(1)).listUsers(any(ListUsersRequest.class), any(StreamObserver.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    @DisplayName("listUsers: empty stream returns empty list")
    void listUsers_emptyStream_returnsEmptyList() {
        doAnswer(invocation -> {
            StreamObserver<UserResponse> observer = invocation.getArgument(1);
            observer.onCompleted();
            return null;
        }).when(asyncStub).listUsers(any(ListUsersRequest.class), any(StreamObserver.class));

        List<UserDto> result = userGrpcClient.listUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(asyncStub, times(1)).listUsers(any(ListUsersRequest.class), any(StreamObserver.class));
    }
}

