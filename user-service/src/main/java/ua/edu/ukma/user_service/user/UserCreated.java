package ua.edu.ukma.user_service.user;

import org.jmolecules.event.types.DomainEvent;

public record UserCreated(Long userId, String username, String email) implements DomainEvent {}
