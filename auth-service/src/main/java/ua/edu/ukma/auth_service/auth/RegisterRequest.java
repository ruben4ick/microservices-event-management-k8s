package ua.edu.ukma.auth_service.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
}

