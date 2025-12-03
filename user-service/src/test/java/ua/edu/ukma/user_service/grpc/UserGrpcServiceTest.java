package ua.edu.ukma.user_service.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.edu.ukma.user_service.user.User;
import ua.edu.ukma.user_service.user.UserRepository;
import ua.edu.ukma.user_service.user.UserRole;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserGrpcService Tests")
class UserGrpcServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StreamObserver<GetUserByIdResponse> getUserByIdObserver;

    @Mock
    private StreamObserver<UserResponse> listUsersObserver;

    @InjectMocks
    private UserGrpcService userGrpcService;

    private User createSampleUser() {
        User user = new User();
        user.setId(1L);
        user.setUserRole(UserRole.USER);
        user.setUsername("testuser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPhoneNumber("+380501234567");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    @DisplayName("getUserById: successful retrieval returns user response")
    void getUserById_success_returnsUserResponse() {
        long userId = 1L;
        User user = createSampleUser();
        GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                .setId(userId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userGrpcService.getUserById(request, getUserByIdObserver);

        ArgumentCaptor<GetUserByIdResponse> responseCaptor = 
                ArgumentCaptor.forClass(GetUserByIdResponse.class);

        verify(getUserByIdObserver, times(1)).onNext(responseCaptor.capture());
        verify(getUserByIdObserver, times(1)).onCompleted();
        verify(getUserByIdObserver, never()).onError(any());

        GetUserByIdResponse response = responseCaptor.getValue();
        assertNotNull(response);
        assertNotNull(response.getUser());
        assertEquals(userId, response.getUser().getId());
        assertEquals("testuser", response.getUser().getUsername());
        assertEquals("John", response.getUser().getFirstName());
        assertEquals("Doe", response.getUser().getLastName());
        assertEquals("john.doe@example.com", response.getUser().getEmail());
    }

    @Test
    @DisplayName("getUserById: user not found returns NOT_FOUND error")
    void getUserById_notFound_returnsNotFoundError() {
        long userId = 999L;
        GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                .setId(userId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        userGrpcService.getUserById(request, getUserByIdObserver);

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);

        verify(getUserByIdObserver, never()).onNext(any());
        verify(getUserByIdObserver, never()).onCompleted();
        verify(getUserByIdObserver, times(1)).onError(errorCaptor.capture());

        Throwable error = errorCaptor.getValue();
        assertTrue(error instanceof StatusRuntimeException);
        StatusRuntimeException statusException = (StatusRuntimeException) error;
        assertEquals(Status.NOT_FOUND.getCode(), statusException.getStatus().getCode());
        assertTrue(statusException.getStatus().getDescription().contains("User with id=999 not found"));
    }

    @Test
    @DisplayName("listUsers: successful retrieval streams all users")
    void listUsers_success_streamsAllUsers() {
        User user1 = createSampleUser();
        user1.setId(1L);
        User user2 = createSampleUser();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setFirstName("Jane");

        ListUsersRequest request = ListUsersRequest.newBuilder().build();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        userGrpcService.listUsers(request, listUsersObserver);

        ArgumentCaptor<UserResponse> responseCaptor = ArgumentCaptor.forClass(UserResponse.class);

        verify(listUsersObserver, times(2)).onNext(responseCaptor.capture());
        verify(listUsersObserver, times(1)).onCompleted();
        verify(listUsersObserver, never()).onError(any());

        List<UserResponse> responses = responseCaptor.getAllValues();
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals("testuser", responses.get(0).getUsername());
        assertEquals(2L, responses.get(1).getId());
        assertEquals("user2", responses.get(1).getUsername());
        assertEquals("Jane", responses.get(1).getFirstName());
    }

    @Test
    @DisplayName("listUsers: empty list streams nothing and completes")
    void listUsers_emptyList_completesWithoutErrors() {
        ListUsersRequest request = ListUsersRequest.newBuilder().build();

        when(userRepository.findAll()).thenReturn(List.of());

        userGrpcService.listUsers(request, listUsersObserver);

        verify(listUsersObserver, never()).onNext(any());
        verify(listUsersObserver, times(1)).onCompleted();
        verify(listUsersObserver, never()).onError(any());
    }

    @Test
    @DisplayName("listUsers: repository exception returns INTERNAL error")
    void listUsers_repositoryException_returnsInternalError() {
        ListUsersRequest request = ListUsersRequest.newBuilder().build();
        RuntimeException repositoryException = new RuntimeException("Database error");

        when(userRepository.findAll()).thenThrow(repositoryException);

        userGrpcService.listUsers(request, listUsersObserver);

        ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);

        verify(listUsersObserver, never()).onNext(any());
        verify(listUsersObserver, never()).onCompleted();
        verify(listUsersObserver, times(1)).onError(errorCaptor.capture());

        Throwable error = errorCaptor.getValue();
        assertTrue(error instanceof StatusRuntimeException);
        StatusRuntimeException statusException = (StatusRuntimeException) error;
        assertEquals(Status.INTERNAL.getCode(), statusException.getStatus().getCode());
        assertTrue(statusException.getStatus().getDescription().contains("Failed to list users"));
        assertEquals(repositoryException, statusException.getCause());
    }

    @Test
    @DisplayName("getUserById: response contains all user fields correctly mapped")
    void getUserById_allFieldsCorrectlyMapped() {
        long userId = 1L;
        User user = createSampleUser();
        user.setPhoneNumber("+380501234567");
        GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                .setId(userId)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userGrpcService.getUserById(request, getUserByIdObserver);

        ArgumentCaptor<GetUserByIdResponse> responseCaptor = 
                ArgumentCaptor.forClass(GetUserByIdResponse.class);

        verify(getUserByIdObserver, times(1)).onNext(responseCaptor.capture());

        UserResponse userResponse = responseCaptor.getValue().getUser();
        assertEquals(1L, userResponse.getId());
        assertEquals("testuser", userResponse.getUsername());
        assertEquals("John", userResponse.getFirstName());
        assertEquals("Doe", userResponse.getLastName());
        assertEquals("john.doe@example.com", userResponse.getEmail());
        assertEquals("+380501234567", userResponse.getPhoneNumber());
    }
}

