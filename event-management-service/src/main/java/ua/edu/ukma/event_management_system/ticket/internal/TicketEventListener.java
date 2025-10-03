package ua.edu.ukma.event_management_system.ticket.internal;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import ua.edu.ukma.event_management_system.event.EventCreated;

@Service
@RequiredArgsConstructor
class TicketEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(TicketEventListener.class);

    @ApplicationModuleListener
    void on(EventCreated event) throws InterruptedException {
        var eventId = event.eventId();
        var eventTitle = event.eventTitle();
        var numberOfTickets = event.numberOfTickets();

        LOG.info("Received event creation for '{}' with {} tickets.", eventTitle, numberOfTickets);

        Thread.sleep(300);

        LOG.info("Finished preparing tickets for event '{}' with id {}.", eventTitle, eventId);
    }
}