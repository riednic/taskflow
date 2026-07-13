package de.riednic.taskflow.common.application

import de.riednic.taskflow.common.persistence.RepositoryResult

sealed interface ServiceResult<out T> {

    sealed interface Success<out T> : ServiceResult<T> {
        val value: T
    }

    data class Ok<out T>(override val value: T) : Success<T>

    interface Error : ServiceResult<Nothing> {
        val code: String
        val category: ErrorCategory
        val message: String
        val throwable: Throwable? get() = null
    }

    data class NotFound(override val message: String = "Resource not found.") : Error {
        override val code = "NOT_FOUND"
        override val category = ErrorCategory.NOT_FOUND
    }

    data class ValidationError(override val message: String) : Error {
        override val code = "VALIDATION_ERROR"
        override val category = ErrorCategory.BAD_REQUEST
    }

    data class Forbidden(override val message: String) : Error {
        override val code = "FORBIDDEN"
        override val category = ErrorCategory.FORBIDDEN
    }

    data class Conflict(override val message: String, override val throwable: Throwable? = null) : Error {
        override val code = "CONFLICT"
        override val category = ErrorCategory.CONFLICT
    }

    data class VersionConflict(override val message: String, override val throwable: Throwable? = null) : Error {
        override val code = "VERSION_CONFLICT"
        override val category = ErrorCategory.CONFLICT
    }

    data class UnexpectedError(override val message: String, override val throwable: Throwable? = null) : Error {
        override val code = "INTERNAL_ERROR"
        override val category = ErrorCategory.INTERNAL
    }
}

enum class ErrorCategory {
    NOT_FOUND,
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    CONFLICT,
    INTERNAL,
}

fun RepositoryResult.Error.toServiceError(): ServiceResult.Error = when (this) {
    is RepositoryResult.NotFound -> ServiceResult.NotFound(message)
    is RepositoryResult.Conflict -> ServiceResult.Conflict(message, throwable)
    is RepositoryResult.VersionConflict -> ServiceResult.VersionConflict(message, throwable)
    is RepositoryResult.UnexpectedError -> ServiceResult.UnexpectedError(message, throwable)
}

fun <T> RepositoryResult<T>.toServiceResult(): ServiceResult<T> = when (this) {
    is RepositoryResult.Success -> ServiceResult.Ok(value)
    is RepositoryResult.Error -> toServiceError()
}
