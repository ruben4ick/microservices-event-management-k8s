package ua.edu.ukma.user_service.user.internal;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.user_service.user.UserManagement;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserManagement userManagement;
    private final ModelMapper modelMapper;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        // TODO: Implement getAllUsers in UserManagement
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        // TODO: Implement getUserById in UserManagement
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        try {
            var user = userManagement.createUser(userDto);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        // TODO: Implement updateUser in UserManagement
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        // TODO: Implement deleteUser in UserManagement
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<User> authenticateUser(@RequestParam String username, @RequestParam String password) {
        // TODO: Implement authenticateUser in UserManagement
        return ResponseEntity.notFound().build();
    }
}
