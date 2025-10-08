package ua.edu.ukma.user_service.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserDto create(UserDto dto) {
        User entity = modelMapper.map(dto, User.class);
        entity.setId(null);
        User saved = userRepository.save(entity);
        return modelMapper.map(saved, UserDto.class);
    }

    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return modelMapper.map(user, UserDto.class);
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .toList();
    }

    public UserDto update(Long id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

        existing.setUserRole(dto.getUserRole());
        existing.setUsername(dto.getUsername());
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setPhoneNumber(dto.getPhoneNumber());
        existing.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getPassword() != null) {
            existing.setPassword(dto.getPassword());
        }

        User saved = userRepository.save(existing);
        return modelMapper.map(saved, UserDto.class);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
