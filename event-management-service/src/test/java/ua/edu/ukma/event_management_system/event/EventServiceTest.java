package ua.edu.ukma.event_management_system.event;

import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.jms.core.JmsTemplate;
import ua.edu.ukma.event_management_system.client.UserClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EventService Tests")
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserClient userClient;

    @Mock
    private JmsTemplate jmsTemplate;

    @InjectMocks
    private EventService eventService;

    private EventDto createSampleDto() {
        EventDto dto = new EventDto();
        dto.setEventTitle("Test Event");
        dto.setDescription("Test Description");
        dto.setDateTimeStart(LocalDateTime.of(2025, 12, 25, 10, 0));
        dto.setDateTimeEnd(LocalDateTime.of(2025, 12, 25, 18, 0));
        dto.setPrice(100.0);
        dto.setCreatorId(1L);
        dto.setNumberOfTickets(50);
        dto.setMinAgeRestriction(18);
        return dto;
    }

    private Event createSampleEntity() {
        Event event = new Event();
        event.setId(1L);
        event.setEventTitle("Test Event");
        event.setDescription("Test Description");
        event.setDateTimeStart(LocalDateTime.of(2025, 12, 25, 10, 0));
        event.setDateTimeEnd(LocalDateTime.of(2025, 12, 25, 18, 0));
        event.setPrice(100.0);
        event.setCreatorId(1L);
        event.setNumberOfTickets(50);
        event.setMinAgeRestriction(18);
        return event;
    }

    @Test
    @DisplayName("create: successful creation returns saved DTO")
    void create_success_returnsSavedDto() {
        EventDto inputDto = createSampleDto();
        Event entity = new Event();
        Event savedEntity = createSampleEntity();
        EventDto savedDto = createSampleDto();
        savedDto.setId(1L);

        when(modelMapper.map(inputDto, Event.class)).thenReturn(entity);
        when(eventRepository.save(entity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, EventDto.class)).thenReturn(savedDto);
        when(userClient.getById(1L)).thenReturn(null);

        EventDto result = eventService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Event", result.getEventTitle());
        verify(eventRepository, times(1)).save(entity);
        verify(jmsTemplate, times(1)).convertAndSend(eq("event.created.topic"), any(), any());
    }

    @Test
    @DisplayName("create: without creatorId skips user validation")
    void create_withoutCreatorId_skipsUserValidation() {
        EventDto inputDto = createSampleDto();
        inputDto.setCreatorId(null);
        Event entity = new Event();
        Event savedEntity = createSampleEntity();
        EventDto savedDto = createSampleDto();
        savedDto.setId(1L);

        when(modelMapper.map(inputDto, Event.class)).thenReturn(entity);
        when(eventRepository.save(entity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, EventDto.class)).thenReturn(savedDto);

        EventDto result = eventService.create(inputDto);

        assertNotNull(result);
        verify(userClient, never()).getById(any());
        verify(eventRepository, times(1)).save(entity);
    }

    @Test
    @DisplayName("create: throws IllegalArgumentException when user not found")
    void create_userNotFound_throwsIllegalArgumentException() {
        EventDto inputDto = createSampleDto();
        Event entity = new Event();
        FeignException.NotFound notFoundException = createNotFoundException();

        when(modelMapper.map(inputDto, Event.class)).thenReturn(entity);
        when(userClient.getById(1L)).thenThrow(notFoundException);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.create(inputDto)
        );

        assertTrue(exception.getMessage().contains("User not found"));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("getById: successful retrieval returns DTO")
    void getById_success_returnsDto() {
        Long eventId = 1L;
        Event event = createSampleEntity();
        EventDto dto = createSampleDto();
        dto.setId(eventId);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(modelMapper.map(event, EventDto.class)).thenReturn(dto);

        EventDto result = eventService.getById(eventId);

        assertNotNull(result);
        assertEquals(eventId, result.getId());
        assertEquals("Test Event", result.getEventTitle());
    }

    @Test
    @DisplayName("getById: event not found throws IllegalArgumentException")
    void getById_notFound_throwsIllegalArgumentException() {
        Long eventId = 999L;

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.getById(eventId)
        );

        assertTrue(exception.getMessage().contains("Event not found"));
    }

    @Test
    @DisplayName("getAll: returns list of all events")
    void getAll_returnsListOfEvents() {
        Event event1 = createSampleEntity();
        event1.setId(1L);
        Event event2 = createSampleEntity();
        event2.setId(2L);
        event2.setEventTitle("Second Event");

        EventDto dto1 = createSampleDto();
        dto1.setId(1L);
        EventDto dto2 = createSampleDto();
        dto2.setId(2L);
        dto2.setEventTitle("Second Event");

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));
        when(modelMapper.map(event1, EventDto.class)).thenReturn(dto1);
        when(modelMapper.map(event2, EventDto.class)).thenReturn(dto2);

        List<EventDto> result = eventService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    @Test
    @DisplayName("update: successful update returns updated DTO")
    void update_success_returnsUpdatedDto() {
        Long eventId = 1L;
        Event existingEvent = createSampleEntity();
        EventDto updateDto = createSampleDto();
        updateDto.setEventTitle("Updated Title");
        Event updatedEntity = createSampleEntity();
        updatedEntity.setEventTitle("Updated Title");
        EventDto updatedDto = createSampleDto();
        updatedDto.setId(eventId);
        updatedDto.setEventTitle("Updated Title");

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
        doNothing().when(modelMapper).map(any(EventDto.class), any(Event.class));
        when(eventRepository.save(existingEvent)).thenReturn(updatedEntity);
        when(modelMapper.map(updatedEntity, EventDto.class)).thenReturn(updatedDto);

        EventDto result = eventService.update(eventId, updateDto);

        assertNotNull(result);
        assertEquals(eventId, result.getId());
        assertEquals("Updated Title", result.getEventTitle());
        verify(modelMapper, times(1)).map(updateDto, existingEvent);
        verify(eventRepository, times(1)).save(existingEvent);
    }

    @Test
    @DisplayName("update: event not found throws IllegalArgumentException")
    void update_notFound_throwsIllegalArgumentException() {
        Long eventId = 999L;
        EventDto updateDto = createSampleDto();

        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> eventService.update(eventId, updateDto)
        );

        assertTrue(exception.getMessage().contains("Event not found"));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete: successfully deletes event")
    void delete_success_deletesEvent() {
        Long eventId = 1L;

        doNothing().when(eventRepository).deleteById(eventId);

        assertDoesNotThrow(() -> eventService.delete(eventId));

        verify(eventRepository, times(1)).deleteById(eventId);
    }

    @Test
    @DisplayName("create: JMS message sent with correct properties")
    void create_jmsMessageSentWithCorrectProperties() {
        EventDto inputDto = createSampleDto();
        Event entity = new Event();
        Event savedEntity = createSampleEntity();
        EventDto savedDto = createSampleDto();
        savedDto.setId(1L);

        when(modelMapper.map(inputDto, Event.class)).thenReturn(entity);
        when(eventRepository.save(entity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, EventDto.class)).thenReturn(savedDto);
        when(userClient.getById(1L)).thenReturn(null);

        eventService.create(inputDto);

        verify(jmsTemplate, times(1)).convertAndSend(
                eq("event.created.topic"),
                eq(savedDto),
                any()
        );
    }

    @Test
    @DisplayName("create: JMS failure does not prevent event creation")
    void create_jmsFailure_doesNotPreventCreation() {
        EventDto inputDto = createSampleDto();
        Event entity = new Event();
        Event savedEntity = createSampleEntity();
        EventDto savedDto = createSampleDto();
        savedDto.setId(1L);

        when(modelMapper.map(inputDto, Event.class)).thenReturn(entity);
        when(eventRepository.save(entity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, EventDto.class)).thenReturn(savedDto);
        when(userClient.getById(1L)).thenReturn(null);
        doThrow(new RuntimeException("JMS error")).when(jmsTemplate).convertAndSend(anyString(), any(), any());

        EventDto result = eventService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(eventRepository, times(1)).save(entity);
    }

    private FeignException.NotFound createNotFoundException() {
        Request req = Request.create(
                Request.HttpMethod.GET,
                "http://user-service/api/users/1",
                Collections.emptyMap(),
                null,
                new feign.RequestTemplate()
        );

        Response resp = Response.builder()
                .request(req)
                .status(404)
                .reason("Not Found")
                .headers(Collections.emptyMap())
                .build();

        return (FeignException.NotFound) FeignException.errorStatus("UserClient#getById", resp);
    }
}

