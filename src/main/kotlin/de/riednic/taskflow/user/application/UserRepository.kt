package de.riednic.taskflow.user.application

import de.riednic.taskflow.user.domain.NewUser
import de.riednic.taskflow.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface UserRepository {
    fun findById(id: UUID): User?
    fun findByEmail(email: String): User?
    fun findAll(pageable: Pageable): Page<User>
    fun save(user: NewUser): User
}
