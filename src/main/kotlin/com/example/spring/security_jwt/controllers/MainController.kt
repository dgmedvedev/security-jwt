package com.example.spring.security_jwt.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class MainController {
    @GetMapping("/unsecured")
    fun unsecuredData(): String {
        return "Unsecured data"
    }

    @GetMapping("/secured")
    fun securedData(): String {
        return "Secured data"
    }

    @GetMapping("/admin")
    fun adminData(): String {
        return "Admin data"
    }

    @GetMapping("/info")
    fun userData(principal: Principal): String {
        return principal.name
    }
}
