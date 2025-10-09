package ua.edu.ukma.event_management_system.event;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
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

    public EventDto create(EventDto dto) {
        Event entity = modelMapper.map(dto, Event.class);
        entity.setId(null);
        if (dto.getCreatorId() != null) {
            ensureUserExists(dto.getCreatorId());
        }
        Event saved = eventRepository.save(entity);
        return modelMapper.map(saved, EventDto.class);
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
        System.out.println("[Retry] Checking if user exists (id=" + id + ")");
        userClient.getById(id);
    }


    @Recover
    void userServiceUnavailable(FeignException ex) {
        throw new IllegalStateException("User-service unavailable", ex);
    }
}
