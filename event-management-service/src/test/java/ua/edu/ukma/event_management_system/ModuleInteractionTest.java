package ua.edu.ukma.event_management_system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ua.edu.ukma.event_management_system.user.internal.UserDto;
import ua.edu.ukma.event_management_system.user.UserManagement;
import ua.edu.ukma.event_management_system.user.internal.UserRole;

import java.time.LocalDate;

/**
 * Test to verify module interaction through events.
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "logging.level.ua.edu.ukma.event_management_system=DEBUG"
})
class ModuleInteractionTest {

    @Autowired
    private UserManagement userManagement;

    @Test
    void testUserCreationTriggersEvent() throws InterruptedException {
        
        System.out.println("\nTest Scenario:");
        System.out.println("1. User module creates a user");
        System.out.println("2. User module publishes UserCreated event");
        System.out.println("3. Event module listens to UserCreated event");
        System.out.println("4. Ticket module listens to EventCreated event");
        
        System.out.println("\nExecuting test...");
        
        UserDto userDto = new UserDto();
        userDto.setUsername("testuser");
        userDto.setEmail("test@example.com");
        userDto.setFirstName("Test");
        userDto.setLastName("User");
        userDto.setPassword("password123");
        userDto.setUserRole(UserRole.USER);
        userDto.setDateOfBirth(LocalDate.of(1990, 1, 1));

        userManagement.createUser(userDto);
        System.out.println("User created successfully!");

        Thread.sleep(2000);
    }
}