package ua.edu.ukma.event_management_system.building.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "building_ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildingRating {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private byte rating;
	@Column(nullable = false)
	private String comment;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "building_id")
	private Building building;
	@Column(name = "author_id", nullable = false)
	private Long authorId; 
}
