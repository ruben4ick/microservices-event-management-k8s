package ua.edu.ukma.event_management_system.event.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.event_management_system.event.EventManagement;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventManagement eventManagement;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        // TODO: Implement getAllEvents in EventManagement
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<Event> getEventById(@PathVariable Long eventId) {
        // TODO: Implement getEventById in EventManagement
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@RequestBody EventDto eventDto) {
        try {
            Event event = eventManagement.createEvent(eventDto);
            return ResponseEntity.ok(event);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long eventId, @RequestBody EventDto eventDto) {
        // TODO: Implement updateEvent in EventManagement
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) {
        // TODO: Implement deleteEvent in EventManagement
        return ResponseEntity.notFound().build();
    }
}
