package de.riednic.taskflow.user.application

import de.riednic.taskflow.user.controller.CreateUserRequest
import de.riednic.taskflow.user.domain.EmailAlreadyExistsException
import de.riednic.taskflow.user.domain.User
import de.riednic.taskflow.user.domain.UserNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID
import kotlin.time.toKotlinInstant

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun createUser(request: CreateUserRequest): User {
        if (userRepository.findByEmail(request.email) != null) {
            throw EmailAlreadyExistsException(request.email)
        }

        val newUser = User(
            name = request.name,
            email = request.email,
            passwordHash = requireNotNull(passwordEncoder.encode(request.rawPassword)),
            role = request.role,
            createdAt = Instant.now().toKotlinInstant(),
        )

        return userRepository.save(newUser)
    }

    fun getUserById(id: UUID): User {
        return userRepository.findById(id) ?: throw UserNotFoundException(id)
    }

    fun getUsers(pageable: Pageable): Page<User> {
        return userRepository.findAll(pageable)
    }
}
