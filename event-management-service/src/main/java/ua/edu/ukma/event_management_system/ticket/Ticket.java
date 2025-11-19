package ua.edu.ukma.event_management_system.ticket;

import jakarta.persistence.*;
import lombok.*;
import ua.edu.ukma.event_management_system.event.Event;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long eventId;
    private String username;
}
