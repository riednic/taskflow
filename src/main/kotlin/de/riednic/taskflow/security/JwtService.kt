package de.riednic.taskflow.security

import de.riednic.taskflow.auth.application.AuthUser
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date

@Service
class JwtService(
    @Value("\${app.jwt.secret}") secret: String,
    @Value("\${app.jwt.expiration-seconds:3600}") val expirationSeconds: Long,
) {

    private val key = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(authUser: AuthUser): String {
        val now = Instant.now()
        return Jwts.builder()
            .subject(authUser.id.toString())
            .claim("role", authUser.authorities)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(expirationSeconds)))
            .signWith(key)
            .compact()
    }

    fun extractUserId(token: String): Long {
        return extractAllClaims(token).subject.toLong()
    }

    fun isValid(token: String, authUser: AuthUser): Boolean {
        val claims = extractAllClaims(token)
        return claims.subject == authUser.id.toString() && claims.expiration.after(Date())
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}
