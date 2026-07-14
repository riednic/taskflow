package de.riednic.taskflow.auth.config

import de.riednic.taskflow.auth.application.AuthUserDetailsService
import de.riednic.taskflow.auth.application.JwtService
import de.riednic.taskflow.auth.application.TokenBlocklist
import de.riednic.taskflow.auth.application.stripBearerPrefix
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthorizationFilter(
    private val jwtService: JwtService,
    private val authUserDetailsService: AuthUserDetailsService,
    private val tokenBlocklist: TokenBlocklist,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        val token = header?.stripBearerPrefix()
        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }

        if (SecurityContextHolder.getContext().authentication == null && !tokenBlocklist.isBlacklisted(token)) {
            try {
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
            } catch (_: JwtException) {
                // Malformed, expired or tampered token: leave the request unauthenticated.
            }
        }

        filterChain.doFilter(request, response)
    }
}
