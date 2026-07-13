package de.riednic.taskflow.common.persistence

sealed interface RepositoryResult<out T> {

    sealed interface Success<out T> : RepositoryResult<T> {
        val value: T
    }

    data class Ok<out T>(override val value: T) : Success<T>

    sealed interface Error : RepositoryResult<Nothing> {
        val message: String
        val throwable: Throwable? get() = null
    }

    data object NotFound : Error {
        override val message = "Resource not found."
    }

    data class Conflict(override val message: String, override val throwable: Throwable? = null) : Error
    data class VersionConflict(override val message: String, override val throwable: Throwable? = null) : Error
    data class UnexpectedError(override val message: String, override val throwable: Throwable? = null) : Error
}
