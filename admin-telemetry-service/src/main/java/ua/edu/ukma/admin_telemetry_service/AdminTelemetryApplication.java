package ua.edu.ukma.admin_telemetry_service;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class AdminTelemetryApplication {
    public static void main(String[] args) { SpringApplication.run(AdminTelemetryApplication.class, args); }
}
