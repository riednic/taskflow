package de.riednic.taskflow.security

import de.riednic.taskflow.auth.application.AuthUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val authUserDetailsService: AuthUserDetailsService,
    private val jwtAuthorizationFilter: JwtAuthorizationFilter,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationProvider(passwordEncoder: PasswordEncoder): AuthenticationProvider =
        DaoAuthenticationProvider(authUserDetailsService).apply {
            setPasswordEncoder(passwordEncoder)
        }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager =
        authenticationConfiguration.authenticationManager

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        authenticationProvider: AuthenticationProvider,
    ): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it
                    .requestMatchers("/auth/login", "/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}
