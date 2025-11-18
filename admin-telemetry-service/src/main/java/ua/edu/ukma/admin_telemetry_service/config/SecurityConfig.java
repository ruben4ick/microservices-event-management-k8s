package ua.edu.ukma.admin_telemetry_service.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {

    private final AdminServerProperties adminServer;

    public SecurityConfig(AdminServerProperties adminServer) {
        this.adminServer = adminServer;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(reg -> reg
                        .requestMatchers(adminServer.path("/assets/**")).permitAll()
                        .requestMatchers(adminServer.path("/login")).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login.loginPage(adminServer.path("/login")).permitAll())
                .logout(logout -> logout.logoutUrl(adminServer.path("/logout")))
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.ignoringRequestMatchers(
                        adminServer.path("/instances"),
                        adminServer.path("/actuator/**")
                ));
        return http.build();
    }
}
