package ua.edu.ukma.event_management_system.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.event_management_system.user.internal.UserDto;
import ua.edu.ukma.event_management_system.user.internal.User;
import ua.edu.ukma.event_management_system.user.internal.UserService;

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
