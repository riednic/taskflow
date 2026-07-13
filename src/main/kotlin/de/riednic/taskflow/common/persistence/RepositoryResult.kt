package de.riednic.taskflow.common.persistence

sealed interface RepositoryResult<out T> {

    sealed interface Success<out T> : RepositoryResult<T> {
        val value: T
    }

    data class Ok<out T>(override val value: T) : Success<T>
    data object Deleted : Success<Unit> {
        override val value = Unit
    }

    data object NotFound : RepositoryResult<Nothing>
    data class Conflict(val message: String, val throwable: Throwable?) : RepositoryResult<Nothing>
    data class VersionConflict(val message: String, val throwable: Throwable?) : RepositoryResult<Nothing>
    data class UnexpectedError(val message: String, val throwable: Throwable?) : RepositoryResult<Nothing>
}
