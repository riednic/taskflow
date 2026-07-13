package de.riednic.taskflow.auth.config

import de.riednic.taskflow.auth.application.AuthUserDetailsService
import de.riednic.taskflow.auth.application.JwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private const val BEARER_PREFIX = "Bearer "

@Component
class JwtAuthorizationFilter(
    private val jwtService: JwtService,
    private val authUserDetailsService: AuthUserDetailsService,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = header.removePrefix(BEARER_PREFIX)

        if (SecurityContextHolder.getContext().authentication == null) {
            val userId = jwtService.extractUserId(token)
            val authUser = authUserDetailsService.loadUserById(userId)

            if (jwtService.isValid(token, authUser)) {
                val authentication = UsernamePasswordAuthenticationToken(
                    authUser,
                    null,
                    authUser.authorities,
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}
