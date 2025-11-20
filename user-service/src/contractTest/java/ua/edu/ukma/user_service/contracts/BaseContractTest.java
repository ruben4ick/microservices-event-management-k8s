package ua.edu.ukma.user_service.contracts;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ua.edu.ukma.user_service.UserServiceApplication;
import ua.edu.ukma.user_service.user.UserDto;
import ua.edu.ukma.user_service.user.UserRole;
import ua.edu.ukma.user_service.user.UserService;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(
		classes = UserServiceApplication.class,
		webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"eureka.client.enabled=false",
		"internal.api.key=INTERNAL_API_KEY",
		"management.otlp.metrics.export.enabled=false",
		"management.otlp.tracing.export.enabled=false",
		"management.otlp.logging.export.enabled=false",
		"management.prometheus.metrics.export.enabled=false"
})
public class BaseContractTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	UserService userService;

	@BeforeEach
	public void setup() {
		when(userService.getById(1L))
				.thenReturn(new UserDto(
						1L,
						UserRole.USER,
						"test_user",
						"Test",
						"User",
						"testemail@email.com",
						"password",
						"+380677777777",
						LocalDate.of(2000, 1, 1)
				));

		when(userService.create(any(UserDto.class)))
				.thenReturn(new UserDto(
						2L,
						UserRole.USER,
						"new_user",
						"New",
						"User",
						"new.user@example.com",
						"securePassword",
						"+380509999999",
						null
				));

		when(userService.update(eq(1L), any(UserDto.class)))
				.thenReturn(new UserDto(
						1L,
						UserRole.USER,
						"test_user_updated",
						"Test",
						"User",
						"testemail@email.com",
						"password",
						"+38068888888",
						LocalDate.of(2000, 1, 1)
				));

		RestAssuredMockMvc.mockMvc(mockMvc);
	}
}
