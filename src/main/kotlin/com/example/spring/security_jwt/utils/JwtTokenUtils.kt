package com.example.spring.security_jwt.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenUtils {
    @Value(JWT_SECRET_PROPERTY)
    private lateinit var secret: String

    @Value(JWT_LIFETIME_PROPERTY)
    private lateinit var jwtLifetime: Duration

    private fun getSigningKey(): SecretKey {
        val keyBytes = secret.toByteArray(Charsets.UTF_8)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateToken(userDetails: UserDetails): String {
        val claims: MutableMap<String, Any> = HashMap()
        val rolesList = userDetails.authorities.map { it.authority }
        claims[ROLES_CLAIM] = rolesList

        val issuedDate = Date()
        val expiredDate = Date(issuedDate.time + jwtLifetime.toMillis())

        return Jwts.builder()
            .claims(claims)
            .subject(userDetails.username)
            .issuedAt(issuedDate)
            .expiration(expiredDate)
            .signWith(getSigningKey())
            .compact()
    }

    fun getUsername(token: String): String {
        return getAllClaimsFromToken(token).subject
    }

    fun getRoles(token: String): List<String> {
        val rolesObject = getAllClaimsFromToken(token)[ROLES_CLAIM]
        if (rolesObject !is List<*>) return emptyList()
        return rolesObject.map { it.toString() }
    }

    private fun getAllClaimsFromToken(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    companion object {
        private const val ROLES_CLAIM = "roles"
        private const val JWT_SECRET_PROPERTY = "\${jwt.secret}"
        private const val JWT_LIFETIME_PROPERTY = "\${jwt.lifetime}"
    }
}
