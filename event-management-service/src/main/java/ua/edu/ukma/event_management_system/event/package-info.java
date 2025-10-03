/**
 * Event module - manages events and event-related operations.
 * 
 * This module provides:
 * - Event CRUD operations
 * - Event scheduling and time management
 * - Event rating system
 * - Event search and filtering
 */
@ApplicationModule(
    displayName = "Event Management",
    allowedDependencies = {"building", "user"}
)
package ua.edu.ukma.event_management_system.event;

import org.springframework.modulith.ApplicationModule;