package com.example.spring.security_jwt.controllers

import com.example.spring.security_jwt.dtos.JwtRequestDto
import com.example.spring.security_jwt.dtos.RegistrationUserDto
import com.example.spring.security_jwt.services.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/auth")
    fun createAuthToken(@RequestBody authRequest: JwtRequestDto): ResponseEntity<*> {
        return authService.createAuthToken(authRequest)
    }

    @PostMapping("/registration")
    fun createNewUser(@RequestBody registrationUserDto: RegistrationUserDto): ResponseEntity<*> {
        return authService.createNewUser(registrationUserDto)
    }
}
