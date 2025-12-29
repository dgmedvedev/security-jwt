package com.example.spring.security_jwt.mappers

import com.example.spring.security_jwt.dtos.RegistrationUserDto
import com.example.spring.security_jwt.dtos.UserDto
import com.example.spring.security_jwt.entities.Role
import com.example.spring.security_jwt.entities.User

fun User.toDto(): UserDto = UserDto(
    id = this.id,
    username = this.username,
    email = this.email,
)

fun RegistrationUserDto.toEntity(password: String, roles: List<Role>): User = User(
    username = this.username,
    password = password,
    email = this.email,
    roles = roles,
)
