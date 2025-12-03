package ua.edu.ukma.user_service.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.jms.core.JmsTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private UserService userService;

    private UserDto createSampleDto() {
        return new UserDto(
                null,
                UserRole.USER,
                "testuser",
                "John",
                "Doe",
                "john.doe@example.com",
                "password123",
                "+380501234567",
                LocalDate.of(1990, 1, 1)
        );
    }

    private User createSampleEntity() {
        User user = new User();
        user.setId(1L);
        user.setUserRole(UserRole.USER);
        user.setUsername("testuser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("password123");
        user.setPhoneNumber("+380501234567");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        return user;
    }

    @Test
    @DisplayName("create: successful creation returns saved DTO")
    void create_success_returnsSavedDto() {
        UserDto inputDto = createSampleDto();
        User entity = new User();
        User savedEntity = createSampleEntity();
        UserDto savedDto = createSampleDto();
        savedDto.setId(1L);

        when(modelMapper.map(inputDto, User.class)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, UserDto.class)).thenReturn(savedDto);

        UserDto result = userService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        verify(userRepository, times(1)).save(entity);
        verify(modelMapper, times(1)).map(inputDto, User.class);
        verify(modelMapper, times(1)).map(savedEntity, UserDto.class);
    }

    @Test
    @DisplayName("create: entity ID is set to null before saving")
    void create_entityIdSetToNull() {
        UserDto inputDto = createSampleDto();
        inputDto.setId(999L);
        User entity = new User();
        entity.setId(999L);
        User savedEntity = createSampleEntity();
        UserDto savedDto = createSampleDto();
        savedDto.setId(1L);

        when(modelMapper.map(inputDto, User.class)).thenReturn(entity);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            assertNull(saved.getId(), "Entity ID should be null before saving");
            return savedEntity;
        });
        when(modelMapper.map(savedEntity, UserDto.class)).thenReturn(savedDto);

        userService.create(inputDto);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("getById: successful retrieval returns DTO")
    void getById_success_returnsDto() {
        Long userId = 1L;
        User user = createSampleEntity();
        UserDto dto = createSampleDto();
        dto.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDto.class)).thenReturn(dto);

        UserDto result = userService.getById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("getById: user not found throws IllegalArgumentException")
    void getById_notFound_throwsIllegalArgumentException() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.getById(userId)
        );

        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("getAll: returns list of all users")
    void getAll_returnsListOfUsers() {
        User user1 = createSampleEntity();
        user1.setId(1L);
        User user2 = createSampleEntity();
        user2.setId(2L);
        user2.setUsername("user2");

        UserDto dto1 = createSampleDto();
        dto1.setId(1L);
        UserDto dto2 = createSampleDto();
        dto2.setId(2L);
        dto2.setUsername("user2");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(modelMapper.map(user1, UserDto.class)).thenReturn(dto1);
        when(modelMapper.map(user2, UserDto.class)).thenReturn(dto2);

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("update: successful update returns updated DTO")
    void update_success_returnsUpdatedDto() {
        Long userId = 1L;
        User existingUser = spy(createSampleEntity());
        UserDto updateDto = createSampleDto();
        updateDto.setFirstName("Jane");
        updateDto.setLastName("Smith");
        updateDto.setPassword(null);

        User updatedUser = createSampleEntity();
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");

        UserDto updatedDto = createSampleDto();
        updatedDto.setId(userId);
        updatedDto.setFirstName("Jane");
        updatedDto.setLastName("Smith");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(modelMapper.map(updatedUser, UserDto.class)).thenReturn(updatedDto);

        UserDto result = userService.update(userId, updateDto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(existingUser);
        verify(existingUser, times(1)).setUserRole(updateDto.getUserRole());
        verify(existingUser, times(1)).setFirstName("Jane");
        verify(existingUser, times(1)).setLastName("Smith");
    }

    @Test
    @DisplayName("update: password updated only when provided")
    void update_passwordUpdatedWhenProvided() {
        Long userId = 1L;
        User existingUser = spy(createSampleEntity());
        UserDto updateDto = createSampleDto();
        updateDto.setPassword("newpassword123");

        User updatedUser = createSampleEntity();
        updatedUser.setPassword("newpassword123");
        UserDto updatedDto = createSampleDto();
        updatedDto.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(modelMapper.map(updatedUser, UserDto.class)).thenReturn(updatedDto);

        userService.update(userId, updateDto);

        verify(existingUser, times(1)).setPassword("newpassword123");
    }

    @Test
    @DisplayName("update: user not found throws IllegalArgumentException")
    void update_notFound_throwsIllegalArgumentException() {
        Long userId = 999L;
        UserDto updateDto = createSampleDto();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.update(userId, updateDto)
        );

        assertTrue(exception.getMessage().contains("User not found"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("update: JMS message sent after successful update")
    void update_jmsMessageSentAfterUpdate() {
        Long userId = 1L;
        User existingUser = createSampleEntity();
        UserDto updateDto = createSampleDto();
        User updatedUser = createSampleEntity();
        UserDto updatedDto = createSampleDto();
        updatedDto.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(modelMapper.map(updatedUser, UserDto.class)).thenReturn(updatedDto);

        userService.update(userId, updateDto);

        verify(jmsTemplate, times(1)).setPubSubDomain(false);
    }

    @Test
    @DisplayName("update: JMS failure does not prevent update")
    void update_jmsFailure_doesNotPreventUpdate() {
        Long userId = 1L;
        User existingUser = createSampleEntity();
        UserDto updateDto = createSampleDto();
        User updatedUser = createSampleEntity();
        UserDto updatedDto = createSampleDto();
        updatedDto.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(modelMapper.map(updatedUser, UserDto.class)).thenReturn(updatedDto);
        doThrow(new RuntimeException("JMS error")).when(jmsTemplate).convertAndSend(eq("user.updates.queue"), any(UserDto.class));

        UserDto result = userService.update(userId, updateDto);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("delete: successfully deletes user")
    void delete_success_deletesUser() {
        Long userId = 1L;

        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> userService.delete(userId));

        verify(userRepository, times(1)).deleteById(userId);
    }
}

