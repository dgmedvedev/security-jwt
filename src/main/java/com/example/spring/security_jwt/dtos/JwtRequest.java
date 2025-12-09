package com.example.spring.security_jwt.dtos;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
