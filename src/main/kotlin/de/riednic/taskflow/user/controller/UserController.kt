package de.riednic.taskflow.user.controller

import de.riednic.taskflow.common.application.ServiceResult
import de.riednic.taskflow.common.controller.toResponseEntity
import de.riednic.taskflow.user.application.UserService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@Validated
@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    fun createUser(
        @Valid @RequestBody request: CreateUserRequest,
        uriComponentsBuilder: UriComponentsBuilder,
    ): ResponseEntity<Any> {
        return when (val result = userService.createUser(request)) {
            is ServiceResult.Success -> {
                val user = result.value.toResponse()
                val location = uriComponentsBuilder.path("/users/{id}").buildAndExpand(user.id).toUri()
                ResponseEntity.created(location).body(user)
            }

            is ServiceResult.Error -> result.toResponseEntity()
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'REVIEWER')")
    @GetMapping
    fun getUsers(@PageableDefault(size = 10) pageable: Pageable): ResponseEntity<Any> {
        return when (val result = userService.getUsers(pageable)) {
            is ServiceResult.Success -> ResponseEntity.ok(result.value.map { it.toResponse() })
            is ServiceResult.Error -> result.toResponseEntity()
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'REVIEWER')")
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<Any> {
        return when (val result = userService.getUserById(id)) {
            is ServiceResult.Success -> ResponseEntity.ok(result.value.toResponse())
            is ServiceResult.Error -> result.toResponseEntity()
        }
    }
}
