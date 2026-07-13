package de.riednic.taskflow.user.domain

import kotlin.time.Instant

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val createdAt: Instant,
) {
    init {
        require(id > 0) { "id must be positive, was $id" }
        require(name.isNotBlank()) { "name must not be blank" }
        require(name.length <= MAX_NAME_LENGTH) { "name must not exceed $MAX_NAME_LENGTH characters" }
        require(email.isNotBlank()) { "email must not be blank" }
        require(passwordHash.isNotBlank()) { "passwordHash must not be blank" }
        require(passwordHash.length <= MAX_PASSWORD_HASH_LENGTH) {
            "passwordHash must not exceed $MAX_PASSWORD_HASH_LENGTH characters"
        }
    }

    companion object {
        const val MAX_NAME_LENGTH = 100
        const val MAX_PASSWORD_HASH_LENGTH = 255
    }
}
