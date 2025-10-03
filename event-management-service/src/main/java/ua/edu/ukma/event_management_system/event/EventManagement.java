package ua.edu.ukma.event_management_system.event;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.ukma.event_management_system.event.internal.EventDto;
import ua.edu.ukma.event_management_system.event.internal.Event;
import ua.edu.ukma.event_management_system.event.internal.EventService;

@Service
@RequiredArgsConstructor
public class EventManagement {

    private final @NonNull ApplicationEventPublisher events;
    private final @NonNull EventService eventService;

    @Transactional
    public Event createEvent(EventDto eventDto) {
        Event event = eventService.createEvent(eventDto);
        
        events.publishEvent(new EventCreated(
            (long) event.getId(), 
            event.getEventTitle(), 
            (long) event.getBuilding().getId(),
            event.getCreator().getId(),
            event.getDateTimeStart(),
            event.getNumberOfTickets()
        ));
        
        return event;
    }
}
