package de.riednic.taskflow.user.domain

// Overthink Exception structure
class UserAlreadyExistsException(email: String) : RuntimeException("User with email '$email' already exists")

class UserNotFoundException(id: Long) : RuntimeException("User with id '$id' not found")
