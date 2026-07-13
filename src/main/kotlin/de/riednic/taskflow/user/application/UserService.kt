package de.riednic.taskflow.user.application

import de.riednic.taskflow.common.application.ServiceResult
import de.riednic.taskflow.common.application.toServiceError
import de.riednic.taskflow.common.application.toServiceResult
import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.user.controller.CreateUserRequest
import de.riednic.taskflow.user.domain.NewUser
import de.riednic.taskflow.user.domain.User
import de.riednic.taskflow.user.domain.UserId
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {

    fun createUser(request: CreateUserRequest): ServiceResult<User> {
        when (val existing = userRepository.findByEmail(request.email)) {
            is RepositoryResult.Success ->
                return UserAlreadyExistsError("User with email '${request.email}' already exists.")

            // Do nothing if existing user was not found since it is not an error here
            is RepositoryResult.NotFound -> Unit
            is RepositoryResult.Error -> return existing.toServiceError()
        }

        val newUser = try {
            NewUser(
                name = request.name,
                email = request.email,
                passwordHash = requireNotNull(passwordEncoder.encode(request.rawPassword)),
                role = request.role,
            )
        } catch (e: IllegalArgumentException) {
            return ServiceResult.ValidationError(e.message ?: "Invalid user data.")
        }

        return userRepository.save(newUser).toServiceResult()
    }

    @Transactional(readOnly = true)
    fun getUserById(id: UserId): ServiceResult<User> {
        return userRepository.findById(id).toServiceResult()
    }

    @Transactional(readOnly = true)
    fun getUsers(pageable: Pageable): ServiceResult<Page<User>> {
        return userRepository.findAll(pageable).toServiceResult()
    }
}
