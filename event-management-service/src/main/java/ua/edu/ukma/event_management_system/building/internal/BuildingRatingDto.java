package ua.edu.ukma.event_management_system.building.internal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BuildingRatingDto {
	private Long id;
	private byte rating;
	private String comment;
	private Long authorId; 
}
