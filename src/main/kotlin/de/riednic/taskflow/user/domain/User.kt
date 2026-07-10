package de.riednic.taskflow.user.domain

import java.util.UUID
import kotlin.time.Instant

class User(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val createdAt: Instant,
)
