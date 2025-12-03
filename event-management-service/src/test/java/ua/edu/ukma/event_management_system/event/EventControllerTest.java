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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
@Import(SecurityConfig.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    @DisplayName("POST /api/events -> creates event and returns 201")
    void create_returnsCreated() throws Exception {
        EventDto inputDto = sampleDto();
        inputDto.setId(null);
        EventDto createdDto = sampleDto();
        createdDto.setId(42L);

        Mockito.when(eventService.create(any(EventDto.class))).thenReturn(createdDto);

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/events/42"))
                .andExpect(jsonPath("$.id", is(42)))
                .andExpect(jsonPath("$.eventTitle", is("Spring Conf")));
    }

    @Test
    @DisplayName("GET /api/events/{id} -> returns event DTO")
    void get_returnsEventDto() throws Exception {
        EventDto dto = sampleDto();
        Mockito.when(eventService.getById(42L)).thenReturn(dto);

        mockMvc.perform(get("/api/events/42"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(42)))
                .andExpect(jsonPath("$.eventTitle", is("Spring Conf")))
                .andExpect(jsonPath("$.price", is(199.99)));
    }

    @Test
    @DisplayName("GET /api/events -> returns list of events")
    void all_returnsListOfEvents() throws Exception {
        EventDto e1 = sampleDto();
        EventDto e2 = new EventDto();
        e2.setId(7L);
        e2.setEventTitle("Kotlin Meetup");

        Mockito.when(eventService.getAll()).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(42)))
                .andExpect(jsonPath("$[0].eventTitle", is("Spring Conf")))
                .andExpect(jsonPath("$[1].id", is(7)))
                .andExpect(jsonPath("$[1].eventTitle", is("Kotlin Meetup")));
    }

    @Test
    @DisplayName("PUT /api/events/{id} -> updates event and returns updated DTO")
    void update_returnsUpdatedDto() throws Exception {
        EventDto updateDto = sampleDto();
        updateDto.setEventTitle("Updated Spring Conf");
        EventDto updatedDto = sampleDto();
        updatedDto.setEventTitle("Updated Spring Conf");

        Mockito.when(eventService.update(eq(42L), any(EventDto.class))).thenReturn(updatedDto);

        mockMvc.perform(put("/api/events/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(42)))
                .andExpect(jsonPath("$.eventTitle", is("Updated Spring Conf")));
    }

    @Test
    @DisplayName("DELETE /api/events/{id} -> returns 204 No Content")
    void delete_returnsNoContent() throws Exception {
        doNothing().when(eventService).delete(42L);

        mockMvc.perform(delete("/api/events/42"))
                .andExpect(status().isNoContent());

        Mockito.verify(eventService, times(1)).delete(42L);
    }

    @Test
    @DisplayName("GET /api/events/{id} -> returns event when found")
    void get_returnsEventWhenFound() throws Exception {
        EventDto dto = sampleDto();
        Mockito.when(eventService.getById(42L)).thenReturn(dto);

        mockMvc.perform(get("/api/events/42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(42)))
                .andExpect(jsonPath("$.eventTitle", is("Spring Conf")));
    }
}

