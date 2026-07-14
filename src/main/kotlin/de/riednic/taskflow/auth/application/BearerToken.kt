package de.riednic.taskflow.auth.application

const val BEARER_PREFIX = "Bearer "

fun String.stripBearerPrefix(): String? =
    if (startsWith(BEARER_PREFIX)) removePrefix(BEARER_PREFIX) else null
