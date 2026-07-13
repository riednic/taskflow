package de.riednic.taskflow.user.controller

import com.fasterxml.jackson.annotation.JsonProperty
import de.riednic.taskflow.user.domain.User
import de.riednic.taskflow.user.domain.UserRole
import kotlin.time.Instant

data class UserResponse(

    @JsonProperty("id")
    val id: Long,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("email")
    val email: String,

    @JsonProperty("role")
    val role: UserRole,

    @JsonProperty("createdAt")
    val createdAt: Instant,
)

fun User.toResponse() = UserResponse(
    id = id.value,
    name = name,
    email = email,
    role = role,
    createdAt = createdAt,
)
