/**
 * Ticket module - manages ticket sales, reservations, and validation.
 * 
 * This module provides:
 * - Ticket purchase and reservation functionality
 * - Ticket validation and verification
 * - Ticket management for events
 * - Integration with payment systems
 * 
 * Dependencies:
 * - event: Event information for ticket association
 * - user: User information for ticket ownership
 */
@ApplicationModule(
    displayName = "Ticket Management",
    allowedDependencies = {"event", "user"}
)
package ua.edu.ukma.event_management_system.ticket;

import org.springframework.modulith.ApplicationModule;