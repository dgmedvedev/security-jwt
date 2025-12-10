package com.example.spring.security_jwt.controllers;

import com.example.spring.security_jwt.dtos.JwtRequestDto;
import com.example.spring.security_jwt.dtos.JwtResponseDto;
import com.example.spring.security_jwt.dtos.RegistrationUserDto;
import com.example.spring.security_jwt.dtos.UserDto;
import com.example.spring.security_jwt.entities.User;
import com.example.spring.security_jwt.exceptions.AppError;
import com.example.spring.security_jwt.services.UserService;
import com.example.spring.security_jwt.utils.JwtTokenUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequestDto authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(
                    new AppError(
                            HttpStatus.UNAUTHORIZED.value(),
                            "Invalid username or password"),
                    HttpStatus.UNAUTHORIZED
            );
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponseDto(token));
    }

    @PostMapping("/registration")
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "Password mismatch"),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (userService.findByUsername(registrationUserDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(
                    new AppError(HttpStatus.BAD_REQUEST.value(), "User already exists"),
                    HttpStatus.BAD_REQUEST
            );
        }
        User user = userService.createNewUser(registrationUserDto);
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername(), user.getEmail()));
    }
}
