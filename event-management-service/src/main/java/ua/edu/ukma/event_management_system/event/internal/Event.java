package ua.edu.ukma.event_management_system.event.internal;

import lombok.Data;
import ua.edu.ukma.event_management_system.building.internal.Building;
import ua.edu.ukma.event_management_system.user.internal.User;

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
    private List<User> users;
    private byte[] image;
    private User creator;
    private double price;
}
