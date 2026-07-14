package de.riednic.taskflow.auth.persistence

import de.riednic.taskflow.auth.application.TokenBlocklist
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.security.MessageDigest
import java.time.Duration
import java.time.Instant

private const val KEY_PREFIX = "auth:blocklist:"

@Repository
class RedisTokenBlocklist(
    private val redisTemplate: StringRedisTemplate,
) : TokenBlocklist {

    override fun blacklist(token: String, expiresAt: Instant) {
        val ttl = Duration.between(Instant.now(), expiresAt)
        if (ttl.isNegative || ttl.isZero) {
            return
        }

        redisTemplate.opsForValue().set(token.toBlocklistKey(), "1", ttl)
    }

    override fun isBlacklisted(token: String): Boolean =
        redisTemplate.hasKey(token.toBlocklistKey()) == true
}

// Hash JWT so they have a consistent length and are not stored as is, for security reasons
private fun String.toBlocklistKey(): String {
    val digest = MessageDigest.getInstance("SHA-256").digest(toByteArray())
    return KEY_PREFIX + digest.joinToString("") { "%02x".format(it) }
}
