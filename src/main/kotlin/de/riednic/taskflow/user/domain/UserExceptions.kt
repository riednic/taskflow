package de.riednic.taskflow.user.domain

import java.util.UUID

class EmailAlreadyExistsException(email: String) : RuntimeException("User with email '$email' already exists")

class UserNotFoundException(id: UUID) : RuntimeException("User with id '$id' not found")
