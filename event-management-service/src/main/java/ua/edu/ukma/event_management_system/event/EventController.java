package ua.edu.ukma.event_management_system.event;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDto> create(@Valid @RequestBody EventDto dto) {
        EventDto created = eventService.create(dto);
        return ResponseEntity.created(URI.create("/api/events/" + created.getId()))
                .body(created);
    }

    @GetMapping("/{id}")
    public EventDto get(@PathVariable Long id) {
        return eventService.getById(id);
    }

    @GetMapping
    public List<EventDto> all() {
        return eventService.getAll();
    }

    @PutMapping("/{id}")
    public EventDto update(@PathVariable Long id, @Valid @RequestBody EventDto dto) {
        return eventService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        eventService.delete(id);
    }

    @GetMapping(value = "/{id}/html", produces = MediaType.TEXT_HTML_VALUE)
    public String viewAsHtml(@PathVariable Long id) {
        EventDto e = eventService.getById(id);
        return """
        <!doctype html>
        <html><head><meta charset="utf-8"><title>%s</title></head>
        <body>
          <h1>%s</h1>
          <p><b>When:</b> %s — %s</p>
          <p><b>Price:</b> %.2f</p>
          <p>%s</p>
        </body></html>
        """.formatted(
                e.getEventTitle(), e.getEventTitle(),
                e.getDateTimeStart(), e.getDateTimeEnd(),
                e.getPrice(), e.getDescription()
        );
    }

    @GetMapping(value = "/export", produces = "text/csv")
    public ResponseEntity<byte[]> exportCsv() {
        var rows = eventService.getAll();
        var sb = new StringBuilder("id,title,start,end,price\n");

        for (var e : rows) {
            sb.append("%d,%s,%s,%s,%.2f"
                            .formatted(e.getId(), e.getEventTitle(),
                                    e.getDateTimeStart(), e.getDateTimeEnd(), e.getPrice()))
                    .append('\n');
        }

        byte[] bytes = sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=events.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }
}
