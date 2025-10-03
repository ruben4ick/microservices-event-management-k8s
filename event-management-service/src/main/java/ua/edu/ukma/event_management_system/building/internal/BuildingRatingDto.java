package ua.edu.ukma.event_management_system.building.internal;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.ukma.event_management_system.user.internal.UserDto;

@Data
@NoArgsConstructor
public class BuildingRatingDto {
	private Long id;
	private byte rating;
	private String comment;
	private UserDto author;
}
