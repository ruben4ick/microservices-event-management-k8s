package ua.edu.ukma.event_management_system.event;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.event_management_system.building.internal.Building;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventTitle;

    private LocalDateTime dateTimeStart;
    private LocalDateTime dateTimeEnd;

    @Column(name = "building_id")
    private Long building;

    @Column(length = 2000)
    private String description;

    private int numberOfTickets;
    private int minAgeRestriction;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_users", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "user_id")
    private List<Long> userIds = new ArrayList<>();

    @Lob
    private byte[] image;

    private Long creatorId;
    private double price;
}
