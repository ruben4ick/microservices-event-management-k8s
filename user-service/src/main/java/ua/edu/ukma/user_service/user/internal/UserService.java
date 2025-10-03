package ua.edu.ukma.user_service.user.internal;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(u -> mapper.map(u, UserDto.class))
                .toList();
    }

    public UserDto createUser(UserDto dto) {
        User user = mapper.map(dto, User.class);
        return mapper.map(userRepository.save(user), UserDto.class);
    }

    public Optional<UserDto> getUser(Long id) {
        return userRepository.findById(id)
                .map(u -> mapper.map(u, UserDto.class));
    }
}
