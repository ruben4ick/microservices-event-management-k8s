package ua.edu.ukma.event_management_system.ticket.internal;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.event_management_system.event.internal.Event;
import ua.edu.ukma.event_management_system.user.internal.User;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class Ticket {
    private long id;
    private User user;
    private Event event;
    private double price;
    private LocalDateTime purchaseDate;
}
