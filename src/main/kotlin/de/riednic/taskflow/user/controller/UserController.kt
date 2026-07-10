package de.riednic.taskflow.user.controller

import de.riednic.taskflow.user.application.UserService
import de.riednic.taskflow.user.domain.User
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Validated
@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {

    @PostMapping
    fun createUser(@Valid @RequestBody request: CreateUserRequest): UserResponse {
        return userService.createUser(request).toResponse()
    }

    @GetMapping
    fun getUsers(@PageableDefault(size = 10) pageable: Pageable): Page<UserResponse> {
        return userService.getUsers(pageable).map { it.toResponse() }
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: UUID): UserResponse {
        return userService.getUserById(id).toResponse()
    }

    private fun User.toResponse() = UserResponse(
        id = id,
        name = name,
        email = email,
        role = role,
        createdAt = createdAt,
    )
}
