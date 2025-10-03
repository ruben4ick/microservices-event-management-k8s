package ua.edu.ukma.event_management_system.ticket.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketDto {
    private long id;
    private long user;
    private long event;
    private double price;
    private LocalDateTime purchaseDate;
}
