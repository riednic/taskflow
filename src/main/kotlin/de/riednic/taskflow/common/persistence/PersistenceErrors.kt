package de.riednic.taskflow.common.persistence

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.orm.ObjectOptimisticLockingFailureException

inline fun <T> catchingPersistenceErrors(
    conflictMessage: String,
    unexpectedErrorMessage: String,
    block: () -> RepositoryResult<T>,
): RepositoryResult<T> = try {
    block()
} catch (_: EmptyResultDataAccessException) {
    RepositoryResult.NotFound
} catch (e: ObjectOptimisticLockingFailureException) {
    markRollbackOnly()
    RepositoryResult.VersionConflict("Task was modified concurrently.", e)
} catch (e: DataIntegrityViolationException) {
    markRollbackOnly()
    RepositoryResult.Conflict(conflictMessage, e)
} catch (e: Exception) {
    markRollbackOnly()
    RepositoryResult.UnexpectedError(unexpectedErrorMessage, e)
}
