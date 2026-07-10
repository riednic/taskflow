package de.riednic.taskflow.user.domain

import java.util.UUID

// TODO: Overthink Exception structure
class UserAlreadyExistsException(email: String) : RuntimeException("User with email '$email' already exists")

class UserNotFoundException(id: UUID) : RuntimeException("User with id '$id' not found")
