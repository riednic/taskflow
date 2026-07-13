package de.riednic.taskflow.user.domain

data class NewUser(
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
) {
    init {
        require(name.isNotBlank()) { "name must not be blank" }
        require(name.length <= User.MAX_NAME_LENGTH) { "name must not exceed ${User.MAX_NAME_LENGTH} characters" }
        require(email.isNotBlank()) { "email must not be blank" }
        require(passwordHash.isNotBlank()) { "passwordHash must not be blank" }
        require(passwordHash.length <= User.MAX_PASSWORD_HASH_LENGTH) {
            "passwordHash must not exceed ${User.MAX_PASSWORD_HASH_LENGTH} characters"
        }
    }
}
