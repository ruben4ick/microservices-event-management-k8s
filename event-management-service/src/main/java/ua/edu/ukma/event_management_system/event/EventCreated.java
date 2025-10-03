package ua.edu.ukma.event_management_system.event;

import org.jmolecules.event.types.DomainEvent;

import java.time.LocalDateTime;

public record EventCreated(Long eventId, String eventTitle, Long buildingId, Long creatorId, 
                          LocalDateTime startTime, int numberOfTickets) implements DomainEvent {}
