package de.riednic.taskflow.auth.application

import de.riednic.taskflow.user.domain.UserRole
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JwtServiceTest {

    private val secret = "unit-test-secret-unit-test-secret-unit-test-secret-32"
    private val authUser = AuthUser(id = 1L, email = "user@example.com", passwordHash = "hash", role = UserRole.MEMBER)

    @Test
    fun `accepts a freshly generated, unexpired token for the same user`() {
        val jwtService = JwtService(secret, expirationSeconds = 3600)
        val token = jwtService.generateToken(authUser)

        assertTrue(jwtService.isValid(token, authUser))
    }

    @Test
    fun `rejects an expired token`() {
        val jwtService = JwtService(secret, expirationSeconds = -1)
        val token = jwtService.generateToken(authUser)

        assertFalse(jwtService.isValid(token, authUser))
    }

    @Test
    fun `rejects a token that was tampered with`() {
        val jwtService = JwtService(secret, expirationSeconds = 3600)
        val token = jwtService.generateToken(authUser)
        val tamperedToken = token.dropLast(1) + if (token.last() == 'A') 'B' else 'A'

        assertFalse(jwtService.isValid(tamperedToken, authUser))
    }

    @Test
    fun `rejects a token signed with a different secret`() {
        val jwtService = JwtService(secret, expirationSeconds = 3600)
        val otherJwtService = JwtService("a-completely-different-secret-value-32-bytes", expirationSeconds = 3600)
        val token = otherJwtService.generateToken(authUser)

        assertFalse(jwtService.isValid(token, authUser))
    }

    @Test
    fun `rejects a valid token when it belongs to a different user`() {
        val jwtService = JwtService(secret, expirationSeconds = 3600)
        val token = jwtService.generateToken(authUser)
        val otherUser = AuthUser(id = 2L, email = "other@example.com", passwordHash = "hash", role = UserRole.MEMBER)

        assertFalse(jwtService.isValid(token, otherUser))
    }
}
