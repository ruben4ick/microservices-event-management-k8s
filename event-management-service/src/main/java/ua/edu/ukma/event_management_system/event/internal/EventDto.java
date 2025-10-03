package ua.edu.ukma.event_management_system.event.internal;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class EventDto {
    private int id;
    private String eventTitle;
    private LocalDateTime dateTimeStart;
    private LocalDateTime dateTimeEnd;
    private long building;
    private String description;
    private int numberOfTickets;
    private int minAgeRestriction;
    private List<Long> rating;
    private List<Long> userIds;
    private byte[] image;
    private Long creatorId;
    private double price;
}
