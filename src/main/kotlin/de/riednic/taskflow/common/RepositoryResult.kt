package de.riednic.taskflow.common

sealed interface RepositoryResult<out T> {

    data class Success<out T>(val value: T) : RepositoryResult<T>
    data object Deleted : RepositoryResult<Nothing>
    data object NotFound : RepositoryResult<Nothing>
    data class Conflict(val message: String, val throwable: Throwable?) : RepositoryResult<Nothing>
    data class VersionConflict(val message: String, val throwable: Throwable?) : RepositoryResult<Nothing>
    data class UnexpectedError(val message: String, val throwable: Throwable?) : RepositoryResult<Nothing>
}
