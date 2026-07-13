package de.riednic.taskflow.user.domain

@JvmInline
value class UserId(val value: Long) {
    override fun toString() = value.toString()
}
