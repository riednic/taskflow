package de.riednic.taskflow.auth.application

import de.riednic.taskflow.auth.controller.LoginResponse
import de.riednic.taskflow.common.application.ServiceResult
import io.jsonwebtoken.JwtException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val tokenBlocklist: TokenBlocklist,
) {

    fun login(email: String, password: String): ServiceResult<LoginResponse> {
        val authentication = try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
        } catch (_: AuthenticationException) {
            return InvalidCredentialsError("Invalid email or password.")
        }

        val authUser = authentication.principal as AuthUser

        return ServiceResult.Ok(
            LoginResponse(
                token = jwtService.generateToken(authUser),
                expiresIn = jwtService.expirationSeconds.toInt(),
            )
        )
    }

    fun logout(authorizationHeader: String): ServiceResult<Unit> {
        val token = authorizationHeader.stripBearerPrefix()
            ?: return ServiceResult.ValidationError("Authorization header must be a Bearer token.")

        val expiresAt = try {
            jwtService.extractExpiration(token)
        } catch (_: JwtException) {
            return ServiceResult.ValidationError("Invalid token.")
        }

        tokenBlocklist.blacklist(token, expiresAt)
        return ServiceResult.Ok(Unit)
    }
}
