package de.riednic.taskflow.auth.application

import de.riednic.taskflow.auth.controller.LoginResponse
import de.riednic.taskflow.security.JwtService
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
) {

    fun login(email: String, password: String): LoginResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(email, password),
        )

        val authUser = authentication.principal as AuthUser

        return LoginResponse(
            token = jwtService.generateToken(authUser),
            expiresIn = jwtService.expirationSeconds.toInt(),
        )
    }
}
