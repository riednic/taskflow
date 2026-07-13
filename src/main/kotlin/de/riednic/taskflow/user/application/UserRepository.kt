package de.riednic.taskflow.user.application

import de.riednic.taskflow.user.domain.NewUser
import de.riednic.taskflow.user.domain.User
import de.riednic.taskflow.user.domain.UserRole
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserRepository {
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?
    fun findAll(pageable: Pageable): Page<User>
    fun findAllByRole(role: UserRole): List<User>
    fun save(user: NewUser): User
}
