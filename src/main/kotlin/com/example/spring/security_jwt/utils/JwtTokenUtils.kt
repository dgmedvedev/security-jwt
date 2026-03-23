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

    @Value(ACCESS_TOKEN_LIFETIME_PROPERTY)
    private lateinit var accessTokenLifetime: Duration

    @Value(REFRESH_TOKEN_LIFETIME_PROPERTY)
    private lateinit var refreshTokenLifetime: Duration

    private fun getSigningKey(): SecretKey {
        val keyBytes = secret.toByteArray(Charsets.UTF_8)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun generateAccessToken(userDetails: UserDetails): String {
        return generateToken(
            userDetails = userDetails,
            tokenType = ACCESS_TOKEN_TYPE,
            tokenLifetime = accessTokenLifetime,
        )
    }

    fun generateRefreshToken(userDetails: UserDetails): String {
        return generateToken(
            userDetails = userDetails,
            tokenType = REFRESH_TOKEN_TYPE,
            tokenLifetime = refreshTokenLifetime,
        )
    }

    private fun generateToken(
        userDetails: UserDetails,
        tokenType: String,
        tokenLifetime: Duration,
    ): String {
        val claims: MutableMap<String, Any> = HashMap()
        val rolesList = userDetails.authorities.map { it.authority }
        claims[ROLES_CLAIM] = rolesList
        claims[TOKEN_TYPE_CLAIM] = tokenType

        val issuedDate = Date()
        val expiredDate = Date(issuedDate.time + tokenLifetime.toMillis())

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

    fun isAccessToken(token: String): Boolean {
        return getAllClaimsFromToken(token)[TOKEN_TYPE_CLAIM] == ACCESS_TOKEN_TYPE
    }

    fun isRefreshToken(token: String): Boolean {
        return getAllClaimsFromToken(token)[TOKEN_TYPE_CLAIM] == REFRESH_TOKEN_TYPE
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
        private const val TOKEN_TYPE_CLAIM = "token_type"
        private const val ACCESS_TOKEN_TYPE = "access"
        private const val REFRESH_TOKEN_TYPE = "refresh"
        private const val JWT_SECRET_PROPERTY = "\${jwt.secret}"
        private const val ACCESS_TOKEN_LIFETIME_PROPERTY = "\${jwt.access-token-lifetime}"
        private const val REFRESH_TOKEN_LIFETIME_PROPERTY = "\${jwt.refresh-token-lifetime}"
    }
}
