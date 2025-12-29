package com.example.spring.security_jwt.dtos

data class UserDto(
    val id: Long,
    val username: String,
    val email: String? = null,
)
