package ua.edu.ukma.event_management_system.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(
        name = "user-service",
        configuration = FeignConfig.class
)
public interface UserClient {
    @GetMapping("/api/users/{id}")
    UserDto getById(@PathVariable("id") Long id);

    @GetMapping("/api/users")
    List<UserDto> getAll();

    @PostMapping("/api/users")
    UserDto create(@RequestBody UserDto dto);

}
