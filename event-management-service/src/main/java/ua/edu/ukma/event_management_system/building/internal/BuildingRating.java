package ua.edu.ukma.event_management_system.building.internal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.event_management_system.user.internal.User;

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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "author_id")
	private User author;
}
