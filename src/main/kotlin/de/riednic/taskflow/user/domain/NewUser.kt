package de.riednic.taskflow.user.domain

class NewUser(
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
)
