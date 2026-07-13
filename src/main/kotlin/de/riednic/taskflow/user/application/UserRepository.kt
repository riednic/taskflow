package de.riednic.taskflow.user.application

import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.user.domain.NewUser
import de.riednic.taskflow.user.domain.User
import de.riednic.taskflow.user.domain.UserId
import de.riednic.taskflow.user.domain.UserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRepository {
    fun findById(id: UserId): RepositoryResult<User>
    fun findByEmail(email: String): RepositoryResult<User>
    fun findAll(pageable: Pageable): RepositoryResult<Page<User>>
    fun findAllByRole(role: UserRole): RepositoryResult<List<User>>
    fun save(user: NewUser): RepositoryResult<User>
}
