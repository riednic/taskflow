package de.riednic.taskflow.auth.application

import de.riednic.taskflow.user.domain.UserRole
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class AuthUser(
    val id: Long,
    email: String,
    passwordHash: String,
    val role: UserRole,
) : User(email, passwordHash, listOf(SimpleGrantedAuthority(role.name)))
