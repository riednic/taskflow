package de.riednic.taskflow.auth.application

import de.riednic.taskflow.auth.controller.LoginResponse
import de.riednic.taskflow.common.application.ServiceResult
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
) {

    fun login(email: String, password: String): ServiceResult<LoginResponse> {
        val authentication = try {
            authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, password))
        } catch (e: AuthenticationException) {
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
}
