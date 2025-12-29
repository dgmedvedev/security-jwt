package com.example.spring.security_jwt.services

import com.example.spring.security_jwt.dtos.RegistrationUserDto
import com.example.spring.security_jwt.entities.User
import com.example.spring.security_jwt.mappers.toEntity
import com.example.spring.security_jwt.repositories.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleService: RoleService,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {

    fun findByUsername(username: String) = userRepository.findByUsername(username)
    fun findByEmail(email: String) = userRepository.findByEmail(email)

    @Transactional
    override fun loadUserByUsername(username: String): UserDetails {
        val user = findByUsername(username).orElseThrow {
            UsernameNotFoundException("User '$username' not found")
        }
        return org.springframework.security.core.userdetails.User(
            user.username,
            user.password,
            user.roles.map { SimpleGrantedAuthority(it.name) }
        )
    }

    fun createNewUser(registrationUserDto: RegistrationUserDto): User {
        val password = passwordEncoder.encode(registrationUserDto.password)
        val roles = listOf(roleService.getUserRole())
        val user = registrationUserDto.toEntity(password, roles)
        return userRepository.save(user)
    }
}
