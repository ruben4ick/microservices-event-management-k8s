package ua.edu.ukma.user_service.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.user_service.user.internal.UserDto;
import ua.edu.ukma.user_service.user.internal.UserService;

@Service
@RequiredArgsConstructor
public class UserManagement {

    private final @NonNull ApplicationEventPublisher events;
    private final @NonNull UserService userService;

    @Transactional
    public UserDto createUser(UserDto userDto) {
        UserDto user = userService.createUser(userDto);
        
        events.publishEvent(new UserCreated(user.getId(), user.getUsername(), user.getEmail()));
        
        return user;
    }
}
