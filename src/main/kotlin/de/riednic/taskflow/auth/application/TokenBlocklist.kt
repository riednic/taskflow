package de.riednic.taskflow.auth.application

import java.time.Instant

interface TokenBlocklist {
    fun blacklist(token: String, expiresAt: Instant)
    fun isBlacklisted(token: String): Boolean
}
