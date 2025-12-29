package com.example.spring.security_jwt.services

import com.example.spring.security_jwt.dtos.JwtRequestDto
import com.example.spring.security_jwt.dtos.JwtResponseDto
import com.example.spring.security_jwt.dtos.RegistrationUserDto
import com.example.spring.security_jwt.exceptions.AppError
import com.example.spring.security_jwt.mappers.toDto
import com.example.spring.security_jwt.utils.JwtTokenUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userService: UserService,
    private val jwtTokenUtils: JwtTokenUtils,
    private val authenticationManager: AuthenticationManager,
) {
    fun createAuthToken(authRequest: JwtRequestDto): ResponseEntity<*> {
        return try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    authRequest.username,
                    authRequest.password
                )
            )
            val userDetails: UserDetails = userService.loadUserByUsername(authRequest.username)
            val token = jwtTokenUtils.generateToken(userDetails)
            ResponseEntity.ok(JwtResponseDto(token))
        } catch (e: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password"))
        }
    }

    fun createNewUser(registrationUserDto: RegistrationUserDto): ResponseEntity<*> {
        if (registrationUserDto.password != registrationUserDto.confirmPassword) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AppError(HttpStatus.BAD_REQUEST.value(), "Password mismatch"))
        }
        if (userService.findByUsername(registrationUserDto.username).isPresent) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(AppError(HttpStatus.BAD_REQUEST.value(), "User already exists"))
        }
        registrationUserDto.email?.let { email ->
            if (userService.findByEmail(email).isPresent) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AppError(HttpStatus.BAD_REQUEST.value(), "Email already exists"))
            }
        }
        val user = userService.createNewUser(registrationUserDto)
        return ResponseEntity.ok(user.toDto())
    }
}
