package ua.edu.ukma.event_management_system.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import ua.edu.ukma.event_management_system.config.SecurityConfig;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@Import(SecurityConfig.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    private EventDto sampleDto() {
        EventDto dto = new EventDto();
        dto.setId(42L);
        dto.setEventTitle("Spring Conf");
        dto.setDescription("All about Spring Framework");
        dto.setDateTimeStart(LocalDateTime.of(2025, 10, 9, 10, 0));
        dto.setDateTimeEnd(LocalDateTime.of(2025, 10, 9, 18, 0));
        dto.setPrice(199.99);
        dto.setCreatorId(1L);
        dto.setNumberOfTickets(100);
        dto.setMinAgeRestriction(16);
        return dto;
    }

    @Test
    @DisplayName("GET /api/events/{id}/html -> text/html")
    void viewAsHtml_returnsHtml() throws Exception {
        EventDto dto = sampleDto();
        Mockito.when(eventService.getById(42L)).thenReturn(dto);

        mockMvc.perform(get("/api/events/42/html"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(containsString("<title>Spring Conf</title>")))
                .andExpect(content().string(containsString("<h1>Spring Conf</h1>")))
                .andExpect(content().string(containsString("<b>When:</b> 2025-10-09T10:00 — 2025-10-09T18:00")))
                .andExpect(content().string(containsString("<b>Price:</b> 199,99")))
                .andExpect(content().string(containsString("All about Spring Framework")));
    }

    @Test
    @DisplayName("GET /api/events/export -> text/csv")
    void exportCsv_returnsCsv() throws Exception {
        EventDto e1 = sampleDto();
        EventDto e2 = new EventDto();
        e2.setId(7L);
        e2.setEventTitle("Kotlin Meetup");
        e2.setDescription("Coroutines and multiplatform");
        e2.setDateTimeStart(LocalDateTime.of(2025, 11, 1, 18, 30));
        e2.setDateTimeEnd(LocalDateTime.of(2025, 11, 1, 21, 0));
        e2.setPrice(0.0);

        Mockito.when(eventService.getAll()).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/api/events/export"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=events.csv"))
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(containsString("id,title,start,end,price\n")))
                .andExpect(content().string(containsString("42,Spring Conf,2025-10-09T10:00,2025-10-09T18:00,199,99")))
                .andExpect(content().string(containsString("7,Kotlin Meetup,2025-11-01T18:30,2025-11-01T21:00,0,00")));
    }
}

