package de.riednic.taskflow.user.persistence

import de.riednic.taskflow.user.domain.UserRole
import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataUserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
    fun findAllByRole(role: UserRole): List<UserEntity>
}
