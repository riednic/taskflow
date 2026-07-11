package de.riednic.taskflow.user.controller

import de.riednic.taskflow.user.domain.UserRole
import kotlin.time.Instant

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: UserRole,
    val createdAt: Instant,
)
