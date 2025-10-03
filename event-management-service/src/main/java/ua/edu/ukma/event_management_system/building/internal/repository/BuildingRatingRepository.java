package ua.edu.ukma.event_management_system.building.internal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.edu.ukma.event_management_system.building.internal.BuildingRating;

@Repository
public interface BuildingRatingRepository extends JpaRepository<BuildingRating, Long> {
}
