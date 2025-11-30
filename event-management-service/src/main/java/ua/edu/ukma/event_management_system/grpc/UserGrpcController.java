package ua.edu.ukma.event_management_system.grpc;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.event_management_system.client.UserDto;

import java.util.List;

@RestController
@RequestMapping("/grpc/users")
@RequiredArgsConstructor
public class UserGrpcController {

    private final UserGrpcClient client;

    @GetMapping
    public List<UserDto> streamAllUsers() {
        return client.listUsers();
    }
}
