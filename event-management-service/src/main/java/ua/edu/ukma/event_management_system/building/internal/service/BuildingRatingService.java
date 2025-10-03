package ua.edu.ukma.event_management_system.building.internal.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ua.edu.ukma.event_management_system.building.internal.Building;
import ua.edu.ukma.event_management_system.building.internal.BuildingRating;
import ua.edu.ukma.event_management_system.building.internal.BuildingRatingDto;
import ua.edu.ukma.event_management_system.building.internal.repository.BuildingRatingRepository;
import ua.edu.ukma.event_management_system.building.internal.repository.BuildingRepository;
import ua.edu.ukma.event_management_system.user.internal.User;
import ua.edu.ukma.event_management_system.user.internal.UserRepository;

@Service
@RequiredArgsConstructor
public class BuildingRatingService {
	private final BuildingRepository buildingRepository;
	private final BuildingRatingRepository ratingRepository;
	private final UserRepository userRepository;
	private final ModelMapper mapper;

	public BuildingRatingDto addRating(Long buildingId, Long userId, byte rating, String comment) {
		Building building = buildingRepository.findById(buildingId)
				.orElseThrow(() -> new RuntimeException("Building not found"));

		User author = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		BuildingRating br = new BuildingRating();
		br.setBuilding(building);
		br.setAuthor(author);
		br.setRating(rating);
		br.setComment(comment);

		return mapper.map(ratingRepository.save(br), BuildingRatingDto.class);
	}
}
