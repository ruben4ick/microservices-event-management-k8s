package ua.edu.ukma.event_management_system.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.edu.ukma.event_management_system.client.UserDto;
import ua.edu.ukma.user_service.grpc.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Service
@RequiredArgsConstructor
public class UserGrpcClient {

    @GrpcClient("user-service")
    private UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userStub;

    @GrpcClient("user-service")
    private UserGrpcServiceGrpc.UserGrpcServiceStub asyncStub;


    public UserResponse getUserById(long userId) {
        GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                .setId(userId)
                .build();

        try {
            GetUserByIdResponse response = userStub.getUserById(request);
            return response.getUser();

        } catch (StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();

            if (code == Status.NOT_FOUND.getCode()) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with id=" + userId + " not found"
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "gRPC error: " + e.getStatus()
            );
        }
    }

    public List<UserDto> listUsers() {
        List<UserDto> result = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        asyncStub.listUsers(
                ListUsersRequest.newBuilder().build(),
                new StreamObserver<>() {

                    @Override
                    public void onNext(UserResponse user) {
                        result.add(toDto(user));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                        latch.countDown();
                    }

                    @Override
                    public void onCompleted() {
                        latch.countDown();
                    }
                });

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    private UserDto toDto(UserResponse u) {
        UserDto dto = new UserDto();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setFirstName(u.getFirstName());
        dto.setLastName(u.getLastName());
        dto.setEmail(u.getEmail());
        return dto;
    }
}
