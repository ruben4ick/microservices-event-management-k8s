package ua.edu.ukma.event_management_system.ticket.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    // TODO: Add TicketManagement service when implemented

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        // TODO: Implement getAllTickets
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long ticketId) {
        // TODO: Implement getTicketById
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Ticket> purchaseTicket(@RequestBody TicketDto ticketDto) {
        // TODO: Implement purchaseTicket
        return ResponseEntity.status(501).build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Ticket>> getTicketsByUser(@PathVariable Long userId) {
        // TODO: Implement getTicketsByUser
        return ResponseEntity.ok(List.of());
    }

    @DeleteMapping("/{ticketId}")
    public ResponseEntity<Void> cancelTicket(@PathVariable Long ticketId) {
        // TODO: Implement cancelTicket
        return ResponseEntity.status(501).build();
    }
}
