package com.example.spring.security_jwt.controllers

import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
@RequiredArgsConstructor
class MainController {

    @GetMapping("/unsecured")
    fun unsecuredData(): String = "Unsecured data"

    @GetMapping("/secured")
    fun securedData(): String = "Secured data"

    @GetMapping("/admin")
    fun adminData(): String = "Admin data"

    @GetMapping("/info")
    fun userData(principal: Principal): String = principal.name
}