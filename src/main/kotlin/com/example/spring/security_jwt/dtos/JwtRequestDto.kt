package com.example.spring.security_jwt.dtos

data class JwtRequestDto(
    val username: String,
    val password: String,
)
