package com.example.spring.security_jwt.dtos

data class RegistrationUserDto(
    val username: String,
    val password: String,
    val confirmPassword: String,
    val email: String? = null,
)
