package ua.edu.ukma.user_service.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class EventTopicListener {
	private final Logger log = LoggerFactory.getLogger(EventTopicListener.class);

	@JmsListener(
			destination = "event.created.topic",
			containerFactory = "topicFactory",
			selector = "eventPrice > 50 AND eventType = 'NEW'"
	)
	public void handleExpensiveEvent(EventDto eventDto) {
		// we don't do anything only log :)
		log.info("Received info about expensive event created with price > 50: {}", eventDto);
	}

	@JmsListener(destination = "event.created.topic", containerFactory = "topicFactory")
	public void handleAllNewEvents(EventDto eventDto) {
		// we don't do anything only log :)
		log.info("Received info about a new event created: {}", eventDto);
	}
}
