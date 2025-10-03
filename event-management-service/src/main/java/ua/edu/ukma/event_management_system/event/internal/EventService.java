package ua.edu.ukma.event_management_system.event.internal;

import org.springframework.stereotype.Service;
import ua.edu.ukma.event_management_system.building.internal.Building;

@Service
public class EventService {

    public Event createEvent(EventDto eventDto) {
        Event event = new Event();
        event.setId((int) System.currentTimeMillis());
        event.setEventTitle(eventDto.getEventTitle());
        event.setDateTimeStart(eventDto.getDateTimeStart());
        event.setDateTimeEnd(eventDto.getDateTimeEnd());
        event.setDescription(eventDto.getDescription());
        event.setNumberOfTickets(eventDto.getNumberOfTickets());
        event.setMinAgeRestriction(eventDto.getMinAgeRestriction());
        event.setPrice(eventDto.getPrice());
        
        Building building = new Building();
        building.setId(eventDto.getBuilding());
        event.setBuilding(building);
        
        event.setCreatorId(eventDto.getCreatorId());
        
        return event;
    }
}
