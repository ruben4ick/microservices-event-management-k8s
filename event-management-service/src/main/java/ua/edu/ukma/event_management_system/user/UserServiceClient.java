package ua.edu.ukma.event_management_system.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserServiceClient {

    private final WebClient.Builder webClientBuilder;

    @Value("${user.service.url}")
    private String userServiceUrl;

    private WebClient getWebClient() {
        return webClientBuilder
                .baseUrl(userServiceUrl)
                .build();
    }

    public Mono<UserDto> getUserById(Long userId) {
        log.info("Fetching user with ID: {}", userId);
        return getWebClient()
                .get()
                .uri("/api/users/{userId}", userId)
                .retrieve()
                .bodyToMono(UserDto.class)
                .doOnError(error -> log.error("Error fetching user with ID {}: {}", userId, error.getMessage()));
    }

    public Mono<List<UserDto>> getAllUsers() {
        log.info("Fetching all users");
        return getWebClient()
                .get()
                .uri("/api/users")
                .retrieve()
                .bodyToFlux(UserDto.class)
                .collectList()
                .doOnError(error -> log.error("Error fetching all users: {}", error.getMessage()));
    }

    public Mono<UserDto> createUser(UserDto userDto) {
        log.info("Creating user: {}", userDto.getUsername());
        return getWebClient()
                .post()
                .uri("/api/users")
                .bodyValue(userDto)
                .retrieve()
                .bodyToMono(UserDto.class)
                .doOnError(error -> log.error("Error creating user: {}", error.getMessage()));
    }

    public Mono<UserDto> authenticateUser(String username, String password) {
        log.info("Authenticating user: {}", username);
        return getWebClient()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/users/authenticate")
                        .queryParam("username", username)
                        .queryParam("password", password)
                        .build())
                .retrieve()
                .bodyToMono(UserDto.class)
                .doOnError(error -> log.error("Error authenticating user {}: {}", username, error.getMessage()));
    }
}
