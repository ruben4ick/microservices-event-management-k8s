package ua.edu.ukma.user_service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, ApiKeyFilter apiKeyFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/manage/prometheus").permitAll()
                        .requestMatchers("/manage/health/**", "/manage/info").permitAll()
                        .requestMatchers("/manage/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    ApiKeyFilter apiKeyFilter(@Value("${internal.api.key}") String expected) {
        return new ApiKeyFilter(expected);
    }

    static class ApiKeyFilter extends OncePerRequestFilter {
        private final String expectedKey;
        ApiKeyFilter(String expectedKey) { this.expectedKey = expectedKey; }

        @Override
        protected void doFilterInternal(@NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain chain)
                throws ServletException, IOException {
            if (req.getRequestURI().startsWith("/manage")) {
                chain.doFilter(req, res);
                return;
            }

            String key = req.getHeader("x-api-key");
            if (key == null || !key.equals(expectedKey)) {
                res.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;
            }

            var auth = new UsernamePasswordAuthenticationToken(
                    "internal",
                    null,
                    List.of(new SimpleGrantedAuthority("INTERNAL"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(req, res);
        }
    }
}
