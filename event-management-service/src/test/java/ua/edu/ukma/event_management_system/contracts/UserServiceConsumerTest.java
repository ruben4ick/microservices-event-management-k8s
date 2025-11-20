package ua.edu.ukma.event_management_system.contracts;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.test.context.TestPropertySource;
import ua.edu.ukma.event_management_system.client.UserClient;
import ua.edu.ukma.event_management_system.client.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureStubRunner(
		ids = {"ua.edu.ukma:user-service:0.0.1-SNAPSHOT:stubs:8081"},
		stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
@TestPropertySource(properties = {
		"user.service.url=http://localhost:8081",
		"eureka.client.enabled=false",
		"internal.api.key=INTERNAL_API_KEY",
		"management.otlp.metrics.export.enabled=false",
		"management.otlp.tracing.export.enabled=false",
		"management.otlp.logging.export.enabled=false",
		"management.prometheus.metrics.export.enabled=false"
})
public class UserServiceConsumerTest {
	@Autowired
	UserClient userClient;

	@Test
	void shouldGetUserById() {
		var user = userClient.getById(1L);

		assertNotNull(user);
		assertEquals(1L, user.getId());
		assertEquals("test_user", user.getUsername());
		assertEquals("testemail@email.com", user.getEmail());
		assertEquals("Test", user.getFirstName());
		assertEquals("User", user.getLastName());
	}

	@Test
	void shouldCreateUser() {
		var newUser = new UserDto();
		newUser.setUsername("new_user");
		newUser.setFirstName("New");
		newUser.setLastName("User");
		newUser.setEmail("new.user@example.com");
		newUser.setUserRole("USER");
		newUser.setPassword("securePassword");
		newUser.setPhoneNumber("+380509999999");

		var created = userClient.create(newUser);

		assertNotNull(created);
		assertEquals(2L, created.getId());
		assertEquals("new_user", created.getUsername());
	}
}
