package ua.edu.ukma.event_management_system.jms;

import feign.FeignException;
import feign.Request;
import feign.Response;
import jakarta.jms.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import ua.edu.ukma.event_management_system.client.UserClient;
import ua.edu.ukma.event_management_system.event.Event;
import ua.edu.ukma.event_management_system.event.EventDto;
import ua.edu.ukma.event_management_system.event.EventRepository;
import ua.edu.ukma.event_management_system.event.EventService;

import java.util.Collections;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JMS Message Tests")
class JmsMessageTest {

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
        dto.setEventTitle("JMS Test Event");
        dto.setDescription("Testing JMS message properties");
        dto.setDateTimeStart(LocalDateTime.of(2025, 12, 25, 10, 0));
        dto.setDateTimeEnd(LocalDateTime.of(2025, 12, 25, 18, 0));
        dto.setPrice(150.0);
        dto.setCreatorId(1L);
        dto.setNumberOfTickets(100);
        dto.setMinAgeRestriction(18);
        return dto;
    }

    private Event createSampleEntity() {
        Event event = new Event();
        event.setId(1L);
        event.setEventTitle("JMS Test Event");
        event.setDescription("Testing JMS message properties");
        event.setDateTimeStart(LocalDateTime.of(2025, 12, 25, 10, 0));
        event.setDateTimeEnd(LocalDateTime.of(2025, 12, 25, 18, 0));
        event.setPrice(150.0);
        event.setCreatorId(1L);
        event.setNumberOfTickets(100);
        event.setMinAgeRestriction(18);
        return event;
    }

    @Test
    @DisplayName("JMS message sent with correct destination and properties")
    void jmsMessage_sentWithCorrectDestinationAndProperties() {
        EventDto inputDto = createSampleDto();
        Event entity = new Event();
        Event savedEntity = createSampleEntity();
        EventDto savedDto = createSampleDto();
        savedDto.setId(1L);

        when(modelMapper.map(inputDto, Event.class)).thenReturn(entity);
        when(eventRepository.save(entity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, EventDto.class)).thenReturn(savedDto);
        when(userClient.getById(1L)).thenReturn(null);

        ArgumentCaptor<MessagePostProcessor> messagePostProcessorCaptor = 
                ArgumentCaptor.forClass(MessagePostProcessor.class);

        eventService.create(inputDto);

        verify(jmsTemplate, times(1)).setPubSubDomain(true);
        verify(jmsTemplate, times(1)).convertAndSend(
                eq("event.created.topic"),
                eq(savedDto),
                messagePostProcessorCaptor.capture()
        );

        MessagePostProcessor postProcessor = messagePostProcessorCaptor.getValue();
        assertNotNull(postProcessor);
    }

    @Test
    @DisplayName("JMS message properties set correctly: eventPrice and eventType")
    void jmsMessage_propertiesSetCorrectly() throws Exception {
        EventDto inputDto = createSampleDto();
        inputDto.setPrice(200.0);
        Event entity = new Event();
        Event savedEntity = createSampleEntity();
        savedEntity.setPrice(200.0);
        EventDto savedDto = createSampleDto();
        savedDto.setId(1L);
        savedDto.setPrice(200.0);

        when(modelMapper.map(inputDto, Event.class)).thenReturn(entity);
        when(eventRepository.save(entity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, EventDto.class)).thenReturn(savedDto);
        when(userClient.getById(1L)).thenReturn(null);

        ArgumentCaptor<MessagePostProcessor> messagePostProcessorCaptor = 
                ArgumentCaptor.forClass(MessagePostProcessor.class);

        eventService.create(inputDto);

        verify(jmsTemplate, times(1)).convertAndSend(
                eq("event.created.topic"),
                eq(savedDto),
                messagePostProcessorCaptor.capture()
        );

        MessagePostProcessor postProcessor = messagePostProcessorCaptor.getValue();
        Message mockMessage = mock(Message.class);
        doNothing().when(mockMessage).setDoubleProperty(anyString(), anyDouble());
        doNothing().when(mockMessage).setStringProperty(anyString(), anyString());

        Message result = postProcessor.postProcessMessage(mockMessage);

        verify(mockMessage, times(1)).setDoubleProperty("eventPrice", 200.0);
        verify(mockMessage, times(1)).setStringProperty("eventType", "NEW");
        assertEquals(mockMessage, result);
    }

    @Test
    @DisplayName("JMS message sent only after successful event creation")
    void jmsMessage_sentOnlyAfterSuccessfulCreation() {
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

        verify(eventRepository, times(1)).save(entity);
        verify(jmsTemplate, times(1)).convertAndSend(
                eq("event.created.topic"),
                eq(savedDto),
                any()
        );
    }

    @Test
    @DisplayName("JMS message not sent when event creation fails")
    void jmsMessage_notSentWhenCreationFails() {
        EventDto inputDto = createSampleDto();
        Event entity = new Event();
        FeignException.NotFound notFoundException = createNotFoundException();

        when(modelMapper.map(inputDto, Event.class)).thenReturn(entity);
        when(userClient.getById(1L)).thenThrow(notFoundException);

        assertThrows(IllegalArgumentException.class, () -> eventService.create(inputDto));

        verify(eventRepository, never()).save(any());
        verify(jmsTemplate, never()).convertAndSend(anyString(), any(), any());
    }

    @Test
    @DisplayName("JMS PubSubDomain set to true for topic")
    void jmsMessage_pubSubDomainSetToTrue() {
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

        verify(jmsTemplate, times(1)).setPubSubDomain(true);
    }

    @Test
    @DisplayName("JMS message contains correct EventDto payload")
    void jmsMessage_containsCorrectPayload() {
        EventDto inputDto = createSampleDto();
        inputDto.setEventTitle("Special Event");
        Event entity = new Event();
        Event savedEntity = createSampleEntity();
        savedEntity.setEventTitle("Special Event");
        EventDto savedDto = createSampleDto();
        savedDto.setId(1L);
        savedDto.setEventTitle("Special Event");

        when(modelMapper.map(inputDto, Event.class)).thenReturn(entity);
        when(eventRepository.save(entity)).thenReturn(savedEntity);
        when(modelMapper.map(savedEntity, EventDto.class)).thenReturn(savedDto);
        when(userClient.getById(1L)).thenReturn(null);

        ArgumentCaptor<EventDto> payloadCaptor = ArgumentCaptor.forClass(EventDto.class);

        eventService.create(inputDto);

        verify(jmsTemplate, times(1)).convertAndSend(
                eq("event.created.topic"),
                payloadCaptor.capture(),
                any()
        );

        EventDto sentPayload = payloadCaptor.getValue();
        assertNotNull(sentPayload);
        assertEquals(1L, sentPayload.getId());
        assertEquals("Special Event", sentPayload.getEventTitle());
        assertEquals(150.0, sentPayload.getPrice());
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

