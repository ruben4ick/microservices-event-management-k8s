package ua.edu.ukma.event_management_system.building.internal.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ua.edu.ukma.event_management_system.building.internal.Building;
import ua.edu.ukma.event_management_system.building.internal.BuildingDto;
import ua.edu.ukma.event_management_system.building.internal.repository.BuildingRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BuildingService {
	private final BuildingRepository buildingRepository;
	private final ModelMapper mapper;

	public List<BuildingDto> getAllBuildings() {
		return buildingRepository.findAll()
				.stream()
				.map(b -> mapper.map(b, BuildingDto.class))
				.toList();
	}

	public Optional<BuildingDto> getBuildingById(Long id) {
		return buildingRepository.findById(id)
				.map(b -> mapper.map(b, BuildingDto.class));
	}

	public BuildingDto createBuilding(BuildingDto dto) {
		Building entity = mapper.map(dto, Building.class);
		return mapper.map(buildingRepository.save(entity), BuildingDto.class);
	}

	public BuildingDto updateBuilding(Long id, BuildingDto dto) {
		return buildingRepository.findById(id)
				.map(existing -> {
					mapper.map(dto, existing); // updates fields
					return mapper.map(buildingRepository.save(existing), BuildingDto.class);
				})
				.orElseThrow(() -> new RuntimeException("Building not found"));
	}

	public void deleteBuilding(Long id) {
		buildingRepository.deleteById(id);
	}
}
