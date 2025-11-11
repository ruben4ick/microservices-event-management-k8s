package ua.edu.ukma.event_management_system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.ConnectionFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import ua.edu.ukma.event_management_system.client.UserDto;

import java.util.Map;

@Configuration
@EnableJms
public class JmsConfig {

	@Bean
	public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
		var converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		converter.setTypeIdMappings(Map.of("ua.edu.ukma.user_service.user.EventDto", UserDto.class));
		converter.setObjectMapper(objectMapper);
		return converter;
	}

	// p2p factory
	@Bean
	public JmsListenerContainerFactory<?> queueFactory(
			ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer,
			MessageConverter jacksonJmsMessageConverter
	) {
		var factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setMessageConverter(jacksonJmsMessageConverter);
		return factory;
	}

	// topic factory
	@Bean
	public JmsListenerContainerFactory<?> topicFactory(
			ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer,
			MessageConverter jacksonJmsMessageConverter
	) {
		var factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		factory.setMessageConverter(jacksonJmsMessageConverter);
		factory.setPubSubDomain(true); // turn on Pub-Sub
		return factory;
	}
}
