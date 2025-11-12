package ua.edu.ukma.event_management_system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
@EnableDiscoveryClient
public class EventManagementSystemApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(EventManagementSystemApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(EventManagementSystemApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Application started successfully!");
	}
}
