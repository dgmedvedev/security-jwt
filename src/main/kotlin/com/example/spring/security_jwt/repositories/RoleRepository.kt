package com.example.spring.security_jwt.repositories

import com.example.spring.security_jwt.entities.Role
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RoleRepository : CrudRepository<Role, Int> {
    fun findByName(name: String): Optional<Role>
}
