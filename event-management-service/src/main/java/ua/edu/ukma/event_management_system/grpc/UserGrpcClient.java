package ua.edu.ukma.event_management_system.grpc;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ua.edu.ukma.user_service.grpc.GetUserByIdRequest;
import ua.edu.ukma.user_service.grpc.GetUserByIdResponse;
import ua.edu.ukma.user_service.grpc.UserGrpcServiceGrpc;
import ua.edu.ukma.user_service.grpc.UserResponse;

@Service
@RequiredArgsConstructor
public class UserGrpcClient {

    @GrpcClient("userService")
    private UserGrpcServiceGrpc.UserGrpcServiceBlockingStub userStub;

    public UserResponse getUserById(long userId) {
        GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                .setId(userId)
                .build();

        GetUserByIdResponse response = userStub.getUserById(request);
        return response.getUser();
    }
}
