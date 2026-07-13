package de.riednic.taskflow.user.controller

import com.fasterxml.jackson.annotation.JsonProperty
import de.riednic.taskflow.user.domain.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateUserRequest(

    @NotBlank
    @Size(max = 100)
    @JsonProperty("name")
    val name: String,

    @NotBlank
    @Email
    @JsonProperty("email")
    val email: String,

    @NotBlank
    @Size(min = 8, max = 256)
    @JsonProperty("password")
    val rawPassword: String,

    @NotNull
    @JsonProperty("role")
    val role: UserRole,
)
