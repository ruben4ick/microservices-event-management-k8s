package ua.edu.ukma.event_management_system.event;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import ua.edu.ukma.event_management_system.client.UserClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

	private final EventRepository eventRepository;
	private final ModelMapper modelMapper;
	private final UserClient userClient;
	private final JmsTemplate jmsTemplate;
	private final Logger log = LoggerFactory.getLogger(EventService.class);

	public EventDto create(EventDto dto) {
		Event entity = modelMapper.map(dto, Event.class);
		entity.setId(null);
		if (dto.getCreatorId() != null) {
			ensureUserExists(dto.getCreatorId());
		}
		Event saved = eventRepository.save(entity);
		var savedDto = modelMapper.map(saved, EventDto.class);

		try {
			jmsTemplate.setPubSubDomain(true);
			jmsTemplate.convertAndSend("event.created.topic", savedDto, message -> {
						message.setDoubleProperty("eventPrice", saved.getPrice());
						message.setStringProperty("eventType", "NEW");
						log.info("Publishing JMS message to event.created.topic - new event");
						return message;
					}
			);
		} catch (Exception e) {
			log.error("Failed sending JMS for event.created.topic for a new event:");
			e.printStackTrace();
		}
		return savedDto;
	}

	public EventDto getById(Long id) {
		Event e = eventRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));
		return modelMapper.map(e, EventDto.class);
	}

	public List<EventDto> getAll() {
		return eventRepository.findAll().stream()
				.map(e -> modelMapper.map(e, EventDto.class))
				.toList();
	}

	public EventDto update(Long id, EventDto dto) {
		Event existing = eventRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));

		modelMapper.map(dto, existing);
		existing.setId(id);

		Event saved = eventRepository.save(existing);
		return modelMapper.map(saved, EventDto.class);
	}

	public void delete(Long id) {
		eventRepository.deleteById(id);
	}

	@Retryable(
			retryFor = FeignException.class,
			backoff = @Backoff(delay = 500, multiplier = 2)
	)
	void ensureUserExists(Long id) {
        log.info("[Retry] Checking if user exists (id={})", id);
		try {
			userClient.getById(id);
		} catch (FeignException.NotFound e) {
			throw new IllegalArgumentException("User not found: " + id, e);
		}
	}


	@Recover
	void userServiceUnavailable(FeignException ex, Long id) {
		if (ex.status() == 404) {
			throw new IllegalArgumentException("User not found: " + id, ex);
		}
		throw new IllegalStateException("User-service unavailable", ex);
	}
}
