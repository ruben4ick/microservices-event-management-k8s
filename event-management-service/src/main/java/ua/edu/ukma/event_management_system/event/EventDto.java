package ua.edu.ukma.event_management_system.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class EventDto {
    private Long id;
    private String eventTitle;
    private LocalDateTime dateTimeStart;
    private LocalDateTime dateTimeEnd;
    private Long building;
    private String description;
    private int numberOfTickets;
    private int minAgeRestriction;
    private List<Long> userIds;
    private byte[] image;
    private Long creatorId;
    private double price;
}
