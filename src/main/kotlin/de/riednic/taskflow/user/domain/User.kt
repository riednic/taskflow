package de.riednic.taskflow.user.domain

import kotlin.time.Instant

class User(
    val id: Long,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val createdAt: Instant,
)
