package ua.edu.ukma.event_management_system.event;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EventRating {
    private long id;
    private Event event;
    private byte rating;
    private Long authorId;
    private String comment;
}
