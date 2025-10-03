package ua.edu.ukma.event_management_system.event.internal;

import lombok.Data;
import ua.edu.ukma.event_management_system.building.internal.Building;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Event {
    private int id;
    private String eventTitle;
    private LocalDateTime dateTimeStart;
    private LocalDateTime dateTimeEnd;
    private Building building;
    private String description;
    private int numberOfTickets;
    private int minAgeRestriction;
    private List<EventRating> rating;
    private List<Long> userIds;
    private byte[] image;
    private Long creatorId;
    private double price;
}
