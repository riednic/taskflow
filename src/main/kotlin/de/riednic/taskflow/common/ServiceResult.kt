package de.riednic.taskflow.common

sealed interface ServiceResult<out T> {

    data class Success<out T>(val value: T) : ServiceResult<T>
    data object Deleted : ServiceResult<Nothing>
    data object NotFound : ServiceResult<Nothing>
    data class ValidationError(val message: String) : ServiceResult<Nothing>
    data class Conflict(val message: String, val throwable: Throwable? = null) : ServiceResult<Nothing>
    data class VersionConflict(val message: String, val throwable: Throwable? = null) : ServiceResult<Nothing>
    data class UnexpectedError(val message: String, val throwable: Throwable? = null) : ServiceResult<Nothing>
}

fun <T> RepositoryResult<T>.toServiceResult(): ServiceResult<T> = when (this) {
    is RepositoryResult.Success -> ServiceResult.Success(value)
    is RepositoryResult.Deleted -> ServiceResult.Deleted
    is RepositoryResult.NotFound -> ServiceResult.NotFound
    is RepositoryResult.Conflict -> ServiceResult.Conflict(message, throwable)
    is RepositoryResult.VersionConflict -> ServiceResult.VersionConflict(message, throwable)
    is RepositoryResult.UnexpectedError -> ServiceResult.UnexpectedError(message, throwable)
}
