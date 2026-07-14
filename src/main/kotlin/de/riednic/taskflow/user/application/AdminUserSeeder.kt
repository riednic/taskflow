package de.riednic.taskflow.user.application

import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.user.domain.NewUser
import de.riednic.taskflow.user.domain.UserRole
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AdminUserSeeder(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${app.admin.name}") private val adminName: String,
    @Value("\${app.admin.email}") private val adminEmail: String,
    @Value("\${app.admin.password}") private val adminPassword: String,
) : ApplicationRunner {

    private val log = LoggerFactory.getLogger(AdminUserSeeder::class.java)

    override fun run(args: ApplicationArguments) {
        val existingAdmins = when (val result = userRepository.findAllByRole(UserRole.ADMIN)) {
            is RepositoryResult.Error -> {
                log.error("Could not check for an existing admin user, skipping seeding: {}", result.message)
                return
            }

            is RepositoryResult.Success -> result.value
        }

        if (existingAdmins.isNotEmpty()) {
            return
        }

        val admin = try {
            NewUser(
                name = adminName,
                email = adminEmail,
                passwordHash = requireNotNull(passwordEncoder.encode(adminPassword)),
                role = UserRole.ADMIN,
            )
        } catch (e: IllegalArgumentException) {
            log.error("Could not seed initial admin user, ADMIN_* configuration is invalid.", e)
            return
        }

        when (val result = userRepository.save(admin)) {
            is RepositoryResult.Error -> log.error("Could not seed initial admin user: {}", result.message)
            is RepositoryResult.Success -> log.info("Seeded initial admin user '{}'.", adminEmail)
        }
    }
}
