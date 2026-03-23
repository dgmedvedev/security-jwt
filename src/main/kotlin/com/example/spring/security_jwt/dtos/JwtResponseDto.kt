package com.example.spring.security_jwt.dtos

data class JwtResponseDto(
    val accessToken: String,
    val refreshToken: String,
)
