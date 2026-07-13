package de.riednic.taskflow.auth.application

import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.user.application.UserRepository
import de.riednic.taskflow.user.domain.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthUserDetailsService(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(email: String): AuthUser {
        val result = userRepository.findByEmail(email)
        if (result !is RepositoryResult.Success) {
            throw UsernameNotFoundException("User with email '$email' not found")
        }

        return result.value.toAuthUser()
    }

    fun loadUserById(id: Long): AuthUser {
        val result = userRepository.findById(id)
        if (result !is RepositoryResult.Success) {
            throw UsernameNotFoundException("User with id '$id' not found")
        }

        return result.value.toAuthUser()
    }

    private fun User.toAuthUser(): AuthUser = AuthUser(
        id = id,
        email = email,
        passwordHash = passwordHash,
        role = role,
    )
}
