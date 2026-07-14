package de.riednic.taskflow.auth.controller

import de.riednic.taskflow.auth.application.AuthService
import de.riednic.taskflow.common.application.ServiceResult
import de.riednic.taskflow.common.controller.toResponseEntity
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<Any> {
        return when (val result = authService.login(request.email, request.password)) {
            is ServiceResult.Success -> ResponseEntity.ok(result.value)
            is ServiceResult.Error -> result.toResponseEntity()
        }
    }

    @PostMapping("/logout")
    fun logout(@RequestHeader(HttpHeaders.AUTHORIZATION) authorizationHeader: String): ResponseEntity<Any> {
        return when (val result = authService.logout(authorizationHeader)) {
            is ServiceResult.Success -> ResponseEntity.noContent().build()
            is ServiceResult.Error -> result.toResponseEntity()
        }
    }
}
