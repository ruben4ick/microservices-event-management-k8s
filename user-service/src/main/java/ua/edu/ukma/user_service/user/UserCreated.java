package ua.edu.ukma.event_management_system.user;

import org.jmolecules.event.types.DomainEvent;

public record UserCreated(Long userId, String username, String email) implements DomainEvent {}
