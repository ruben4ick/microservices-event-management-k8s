package ua.edu.ukma.event_management_system.ticket;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {


    private final TicketService ticketService;

    @PostMapping
    public TicketDto create(@Valid @RequestBody TicketDto dto) {
        return ticketService.create(dto);
    }

    @GetMapping("/{id}")
    public TicketDto get(@PathVariable Long id) {
        return ticketService.getById(id);
    }

    @GetMapping
    public List<TicketDto> all() {
        return ticketService.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        ticketService.delete(id);
    }
}
