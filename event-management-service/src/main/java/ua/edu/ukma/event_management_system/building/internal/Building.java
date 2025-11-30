package ua.edu.ukma.event_management_system.building.internal;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buildings")
@Data
@NoArgsConstructor
public class Building {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private int hourlyRate;
    @Column(nullable = false)
    private int areaM2;
    @Column(nullable = false)
    private int capacity;
    @Column(nullable = false)
    private String description;
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BuildingRating> ratings = new ArrayList<>();

    public Building(Long id, String address, int hourlyRate, int areaM2, int capacity,
                    String description) {
        this.id = id;
        this.address = address;
        this.hourlyRate = hourlyRate;
        this.areaM2 = areaM2;
        this.capacity = capacity;
        this.description = description;
        this.ratings = new ArrayList<>();
    }
}
