package de.riednic.taskflow.user.controller

import de.riednic.taskflow.user.domain.UserRole
import java.util.UUID
import kotlin.time.Instant

data class UserResponse(
    val id: UUID,
    val name: String,
    val email: String,
    val role: UserRole,
    val createdAt: Instant,
)
