package ua.edu.ukma.event_management_system.building.internal.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ua.edu.ukma.event_management_system.building.internal.Building;
import ua.edu.ukma.event_management_system.building.internal.BuildingRating;
import ua.edu.ukma.event_management_system.building.internal.BuildingRatingDto;
import ua.edu.ukma.event_management_system.building.internal.repository.BuildingRatingRepository;
import ua.edu.ukma.event_management_system.building.internal.repository.BuildingRepository;
import ua.edu.ukma.event_management_system.user.UserServiceClient;

@Service
@RequiredArgsConstructor
public class BuildingRatingService {
	private final BuildingRepository buildingRepository;
	private final BuildingRatingRepository ratingRepository;
	private final UserServiceClient userServiceClient;
	private final ModelMapper mapper;

	public BuildingRatingDto addRating(Long buildingId, Long userId, byte rating, String comment) {
		Building building = buildingRepository.findById(buildingId)
				.orElseThrow(() -> new RuntimeException("Building not found"));

		userServiceClient.getUserById(userId)
				.doOnError(error -> {
					throw new RuntimeException("User not found in user-service: " + error.getMessage());
				})
				.block(); 

		BuildingRating br = new BuildingRating();
		br.setBuilding(building);
		br.setAuthorId(userId);
		br.setRating(rating);
		br.setComment(comment);

		return mapper.map(ratingRepository.save(br), BuildingRatingDto.class);
	}
}
