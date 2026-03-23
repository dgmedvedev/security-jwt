package com.example.spring.security_jwt.services

import com.example.spring.security_jwt.dtos.JwtRequestDto
import com.example.spring.security_jwt.dtos.JwtResponseDto
import com.example.spring.security_jwt.dtos.RefreshTokenRequestDto
import com.example.spring.security_jwt.dtos.RegistrationUserDto
import com.example.spring.security_jwt.exceptions.AppError
import io.jsonwebtoken.JwtException
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
            val accessToken = jwtTokenUtils.generateAccessToken(userDetails)
            val refreshToken = jwtTokenUtils.generateRefreshToken(userDetails)
            ResponseEntity.ok(JwtResponseDto(accessToken = accessToken, refreshToken = refreshToken))
        } catch (e: BadCredentialsException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password"))
        }
    }

    fun refreshAuthToken(refreshRequest: RefreshTokenRequestDto): ResponseEntity<*> {
        return try {
            val refreshToken = refreshRequest.refreshToken
            if (!jwtTokenUtils.isRefreshToken(refreshToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AppError(HttpStatus.UNAUTHORIZED.value(), "Invalid refresh token"))
            }
            val username = jwtTokenUtils.getUsername(refreshToken)
            val userDetails = userService.loadUserByUsername(username)
            val newAccessToken = jwtTokenUtils.generateAccessToken(userDetails)
            val newRefreshToken = jwtTokenUtils.generateRefreshToken(userDetails)
            ResponseEntity.ok(
                JwtResponseDto(
                    accessToken = newAccessToken,
                    refreshToken = newRefreshToken,
                )
            )
        } catch (e: JwtException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AppError(HttpStatus.UNAUTHORIZED.value(), "Refresh token expired or invalid"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(AppError(HttpStatus.UNAUTHORIZED.value(), "Refresh token authentication failed"))
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
