package de.riednic.taskflow.user.persistence

import de.riednic.taskflow.user.domain.NewUser
import de.riednic.taskflow.user.domain.User
import de.riednic.taskflow.user.domain.UserRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import kotlin.time.toKotlinInstant

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
class UserEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(nullable = false, unique = true, columnDefinition = "citext")
    var email: String,

    @Column(name = "password_hash", nullable = false)
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(nullable = false, columnDefinition = "user_role")
    var role: UserRole,

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: Instant? = null,
)

fun NewUser.toEntity(): UserEntity = UserEntity(
    name = name,
    email = email,
    passwordHash = passwordHash,
    role = role,
)

@Throws(IllegalArgumentException::class)
fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    passwordHash = passwordHash,
    role = role,
    createdAt = requireNotNull(createdAt) {
        "createdAt was not populated by JPA auditing for UserEntity id=$id"
    }.toKotlinInstant(),
)
