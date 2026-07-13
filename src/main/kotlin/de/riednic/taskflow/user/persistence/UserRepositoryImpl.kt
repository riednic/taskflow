package de.riednic.taskflow.user.persistence

import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.common.persistence.catchingPersistenceErrors
import de.riednic.taskflow.common.persistence.markRollbackOnly
import de.riednic.taskflow.user.application.UserRepository
import de.riednic.taskflow.user.domain.NewUser
import de.riednic.taskflow.user.domain.User
import de.riednic.taskflow.user.domain.UserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class UserRepositoryImpl(
    private val springDataUserRepository: SpringDataUserRepository,
) : UserRepository {

    override fun findById(id: Long): RepositoryResult<User> {
        val userEntity = springDataUserRepository.findById(id).getOrNull() ?: return RepositoryResult.NotFound
        return userEntity.toDomainResult("Could not map saved user to domain model.")
    }

    override fun findByEmail(email: String): RepositoryResult<User> {
        val userEntity = springDataUserRepository.findByEmail(email) ?: return RepositoryResult.NotFound
        return userEntity.toDomainResult("Could not map saved user to domain model.")
    }

    override fun findAll(pageable: Pageable): RepositoryResult<Page<User>> {
        return springDataUserRepository.findAll(pageable)
            .toDomainResult("Could not map saved users to domain model.")
    }

    override fun findAllByRole(role: UserRole): RepositoryResult<List<User>> {
        return springDataUserRepository.findAllByRole(role)
            .toDomainResult("Could not map saved users to domain model.")
    }

    override fun save(user: NewUser): RepositoryResult<User> = catchingPersistenceErrors(
        conflictMessage = "User could not be saved due to a data conflict.",
        unexpectedErrorMessage = "Unexpected error while saving user.",
    ) {
        springDataUserRepository.saveAndFlush(user.toEntity())
            .toDomainResult("Could not map saved user to domain model.")
    }
}

private fun UserEntity.toDomainResult(errorMessage: String): RepositoryResult<User> = try {
    RepositoryResult.Ok(toDomain())
} catch (e: IllegalArgumentException) {
    markRollbackOnly()
    RepositoryResult.UnexpectedError(errorMessage, e)
}

private fun Page<UserEntity>.toDomainResult(errorMessage: String): RepositoryResult<Page<User>> = try {
    RepositoryResult.Ok(map { it.toDomain() })
} catch (e: IllegalArgumentException) {
    markRollbackOnly()
    RepositoryResult.UnexpectedError(errorMessage, e)
}

private fun List<UserEntity>.toDomainResult(errorMessage: String): RepositoryResult<List<User>> = try {
    RepositoryResult.Ok(map { it.toDomain() })
} catch (e: IllegalArgumentException) {
    markRollbackOnly()
    RepositoryResult.UnexpectedError(errorMessage, e)
}
