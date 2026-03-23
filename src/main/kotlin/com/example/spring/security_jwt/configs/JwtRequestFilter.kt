package com.example.spring.security_jwt.configs

import com.example.spring.security_jwt.utils.JwtTokenUtils
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.security.SignatureException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtRequestFilter(
    private val jwtTokenUtils: JwtTokenUtils
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtRequestFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        var username: String? = null
        var jwt: String? = null

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7)
            try {
                username = jwtTokenUtils.getUsername(jwt)
            } catch (e: ExpiredJwtException) {
                log.debug("Token expired")
            } catch (e: SignatureException) {
                log.debug("Signature is invalid")
            }
        }

        if (username != null && jwt != null && SecurityContextHolder.getContext().authentication == null) {
            if (!jwtTokenUtils.isAccessToken(jwt)) {
                filterChain.doFilter(request, response)
                return
            }
            val token = UsernamePasswordAuthenticationToken(
                username,
                null,
                jwtTokenUtils.getRoles(jwt).map { SimpleGrantedAuthority(it) }
            )
            SecurityContextHolder.getContext().authentication = token
        }

        filterChain.doFilter(request, response)
    }
}
