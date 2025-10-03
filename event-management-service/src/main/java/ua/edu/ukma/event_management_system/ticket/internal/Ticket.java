package ua.edu.ukma.event_management_system.ticket.internal;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.event_management_system.event.internal.Event;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class Ticket {
    private long id;
    private Long userId;
    private Event event;
    private double price;
    private LocalDateTime purchaseDate;
}
