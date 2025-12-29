package com.example.spring.security_jwt.services

import com.example.spring.security_jwt.entities.Role
import com.example.spring.security_jwt.repositories.RoleRepository
import org.springframework.stereotype.Service

@Service
class RoleService(
    private val roleRepository: RoleRepository
) {
    fun getUserRole(): Role {
        return roleRepository.findByName("ROLE_USER")
            .orElseThrow { RuntimeException("Role not found") }
    }
}
