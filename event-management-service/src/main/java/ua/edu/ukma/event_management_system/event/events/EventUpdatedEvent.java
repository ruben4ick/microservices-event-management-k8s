package ua.edu.ukma.event_management_system.event.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;
import java.time.LocalDateTime;

@Getter
public class EventUpdatedEvent extends ApplicationEvent {
    
    private final Long eventId;
    private final String eventTitle;
    private final Long buildingId;
    private final LocalDateTime startTime;
    private final int numberOfTickets;
    
    public EventUpdatedEvent(Object source, Long eventId, String eventTitle, Long buildingId,
                           LocalDateTime startTime, int numberOfTickets) {
        super(source);
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.buildingId = buildingId;
        this.startTime = startTime;
        this.numberOfTickets = numberOfTickets;
    }

}
