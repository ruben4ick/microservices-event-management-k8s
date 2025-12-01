package ua.edu.ukma.auth_service.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

