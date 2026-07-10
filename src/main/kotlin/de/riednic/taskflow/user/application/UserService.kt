package de.riednic.taskflow.user.application

import de.riednic.taskflow.user.controller.CreateUserRequest
import de.riednic.taskflow.user.domain.NewUser
import de.riednic.taskflow.user.domain.User
import de.riednic.taskflow.user.domain.UserAlreadyExistsException
import de.riednic.taskflow.user.domain.UserNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun createUser(request: CreateUserRequest): User {
        if (userRepository.findByEmail(request.email) != null) {
            throw UserAlreadyExistsException(request.email)
        }

        val newUser = NewUser(
            name = request.name,
            email = request.email,
            passwordHash = requireNotNull(passwordEncoder.encode(request.rawPassword)),
            role = request.role,
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
