package ua.edu.ukma.user_service.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ua.edu.ukma.user_service.user.User;
import ua.edu.ukma.user_service.user.UserRepository;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class UserGrpcService extends UserGrpcServiceGrpc.UserGrpcServiceImplBase {

    private final UserRepository userRepository;

    @Override
    public void getUserById(GetUserByIdRequest request,
                            StreamObserver<GetUserByIdResponse> responseObserver) {
        long id = request.getId();

        userRepository.findById(id).ifPresentOrElse(user -> {
            UserResponse userResponse = toUserResponse(user);

            GetUserByIdResponse response = GetUserByIdResponse.newBuilder()
                    .setUser(userResponse)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }, () -> {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("User with id=%d not found".formatted(id))
                            .asRuntimeException()
            );
        });
    }

    @Override
    public void listUsers(ListUsersRequest request,
                          StreamObserver<ListUsersResponse> responseObserver) {
        List<User> users = userRepository.findAll();

        ListUsersResponse.Builder builder = ListUsersResponse.newBuilder();
        for (User user : users) {
            builder.addUsers(toUserResponse(user));
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.newBuilder()
                .setId(user.getId())
                .setUsername(user.getUsername())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPhoneNumber(user.getPhoneNumber())
                .build();
    }
}
