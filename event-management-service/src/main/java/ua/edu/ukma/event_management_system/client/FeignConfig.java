package ua.edu.ukma.event_management_system.client;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor apiKeyInterceptor(
            @Value("${internal.api.key}") String apiKey) {
        return template -> template.header("x-api-key", apiKey);
    }
}
