package ua.edu.ukma.event_management_system.event.internal;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.event_management_system.user.internal.User;

@Data
@NoArgsConstructor
public class EventRating {
    private long id;
    private Event event;
    private byte rating;
    private User author;
    private String comment;
}
