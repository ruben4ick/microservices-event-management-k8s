package ua.edu.ukma.user_service.user;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final JmsTemplate jmsTemplate;
	private final Logger log = LoggerFactory.getLogger(UserService.class);

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
		var savedDto = modelMapper.map(saved, UserDto.class);

		try {
			jmsTemplate.setPubSubDomain(false); // p2p
			jmsTemplate.convertAndSend("user.updates.queue", savedDto);
			log.info("Sent P2P message about user id={} being updated", savedDto.getId());
		} catch (Exception e) {
			log.error("Failed to send JMS message about updating user");
			e.printStackTrace();
		}

		return savedDto;
	}

	public void delete(Long id) {
		userRepository.deleteById(id);
	}
}
