package de.riednic.taskflow.user.persistence

import de.riednic.taskflow.user.application.UserRepository
import de.riednic.taskflow.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.util.UUID
import kotlin.jvm.optionals.getOrNull
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant

@Repository
class UserRepositoryImpl(
    private val springDataUserRepository: SpringDataUserRepository,
) : UserRepository {

    override fun findById(id: UUID): User? =
        springDataUserRepository.findById(id).getOrNull()?.toDomain()

    override fun findByEmail(email: String): User? =
        springDataUserRepository.findByEmail(email)?.toDomain()

    override fun findAll(pageable: Pageable): Page<User> =
        springDataUserRepository.findAll(pageable).map { it.toDomain() }

    override fun save(user: User): User =
        springDataUserRepository.save(user.toEntity()).toDomain()

    private fun UserEntity.toDomain() = User(
        id = id,
        name = name,
        email = email,
        passwordHash = passwordHash,
        role = role,
        createdAt = createdAt.toKotlinInstant(),
    )

    private fun User.toEntity() = UserEntity(
        id = id,
        name = name,
        email = email,
        passwordHash = passwordHash,
        role = role,
        createdAt = createdAt.toJavaInstant(),
    )
}
