package ua.edu.ukma.event_management_system.building.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.event_management_system.building.internal.service.BuildingRatingService;
import ua.edu.ukma.event_management_system.building.internal.service.BuildingService;

import java.util.List;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;
    private final BuildingRatingService ratingService;

    @GetMapping
    public List<BuildingDto> getAllBuildings() {
        return buildingService.getAllBuildings();
    }

    @GetMapping("/{buildingId}")
    public ResponseEntity<BuildingDto> getBuildingById(@PathVariable Long buildingId) {
        return buildingService.getBuildingById(buildingId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BuildingDto> createBuilding(@RequestBody BuildingDto dto) {
        return ResponseEntity.ok(buildingService.createBuilding(dto));
    }

    @PutMapping("/{buildingId}")
    public ResponseEntity<BuildingDto> updateBuilding(
            @PathVariable Long buildingId, @RequestBody BuildingDto dto) {
        return ResponseEntity.ok(buildingService.updateBuilding(buildingId, dto));
    }

    @DeleteMapping("/{buildingId}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable Long buildingId) {
        buildingService.deleteBuilding(buildingId);
        return ResponseEntity.noContent().build();
    }
}
