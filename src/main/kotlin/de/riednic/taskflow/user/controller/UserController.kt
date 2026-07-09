package de.riednic.taskflow.user.controller

import de.riednic.taskflow.user.application.UserService
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {

    @PostMapping
    fun createUser(@Valid @RequestBody request: CreateUserRequest): UserResponse {
        TODO("Not yet implemented")
    }

    @GetMapping("/{id}")
    fun getUser(@Positive @PathVariable id: Long): UserResponse {
        TODO("Not yet implemented")
    }
}