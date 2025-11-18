package ua.edu.ukma.user_service.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ua.edu.ukma.user_service.user.UserDto;

@Component
public class UserUpdateListener {

	private final Logger log = LoggerFactory.getLogger(UserUpdateListener.class);

	@JmsListener(
			destination = "user.updates.queue",
			containerFactory = "queueFactory"
	)
	public void handleUpdatingUser(UserDto userDto) {
		log.info("Received P2P message about user update. Certainly doing something.");
	}
}
