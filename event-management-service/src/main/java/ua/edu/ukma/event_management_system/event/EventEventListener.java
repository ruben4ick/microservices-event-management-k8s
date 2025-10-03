package ua.edu.ukma.event_management_system.event;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import ua.edu.ukma.event_management_system.user.UserCreated;

@Service
@RequiredArgsConstructor
class EventEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(EventEventListener.class);

    @ApplicationModuleListener
    void on(UserCreated event) throws InterruptedException {
        var userId = event.userId();
        var username = event.username();

        LOG.info("Received user creation for {}.", username);

        Thread.sleep(500);

        LOG.info("Finished processing user creation for {} with id {}", username, userId);
    }
}
