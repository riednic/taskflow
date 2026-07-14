package de.riednic.taskflow.task.persistence

import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.common.persistence.catchingPersistenceErrors
import de.riednic.taskflow.common.persistence.markRollbackOnly
import de.riednic.taskflow.task.application.TaskRepository
import de.riednic.taskflow.task.domain.NewTask
import de.riednic.taskflow.task.domain.ReplacementTask
import de.riednic.taskflow.task.domain.Task
import de.riednic.taskflow.task.domain.TaskFilter
import de.riednic.taskflow.task.domain.TaskId
import de.riednic.taskflow.task.domain.TaskStatus
import de.riednic.taskflow.task.domain.UpdatedTask
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import kotlin.jvm.optionals.getOrNull

@Repository
class TaskRepositoryImpl(
    private val springDataTaskRepository: SpringDataTaskRepository,
) : TaskRepository {

    override fun findById(id: TaskId): RepositoryResult<Task> {
        val taskEntity = springDataTaskRepository.findById(id.value).getOrNull() ?: return RepositoryResult.NotFound
        return taskEntity.toDomainResult("Could not map saved task to domain model.")
    }

    override fun findAll(filter: TaskFilter, pageable: Pageable): RepositoryResult<Page<Task>> {
        return springDataTaskRepository.findAll(filter.toSpecification(), pageable)
            .toDomainResult("Could not map saved tasks to domain model.")
    }

    override fun save(newTask: NewTask): RepositoryResult<Task> = catchingPersistenceErrors(
        conflictMessage = "Task could not be saved due to a data conflict.",
        unexpectedErrorMessage = "Unexpected error while saving task.",
    ) {
        springDataTaskRepository.saveAndFlush(newTask.toEntity())
            .toDomainResult("Could not map saved task to domain model.")
    }

    override fun replace(replacementTask: ReplacementTask): RepositoryResult<Task> {
        val savedTaskEntity = springDataTaskRepository.findById(replacementTask.id.value).getOrNull()
            ?: return RepositoryResult.NotFound

        savedTaskEntity.checkVersion(replacementTask.version)?.let { return it }

        savedTaskEntity.title = replacementTask.title
        savedTaskEntity.description = replacementTask.description
        savedTaskEntity.priority = replacementTask.priority
        savedTaskEntity.assignedTo = replacementTask.assignedTo?.value

        return catchingPersistenceErrors(
            conflictMessage = "Task could not be updated due to a data conflict.",
            unexpectedErrorMessage = "Unexpected error while updating task.",
        ) {
            springDataTaskRepository.saveAndFlush(savedTaskEntity)
                .toDomainResult("Could not map updated task to domain model.")
        }
    }

    override fun update(updatedTask: UpdatedTask): RepositoryResult<Task> {
        val savedTaskEntity = springDataTaskRepository.findById(updatedTask.id.value).getOrNull()
            ?: return RepositoryResult.NotFound

        savedTaskEntity.checkVersion(updatedTask.version)?.let { return it }

        savedTaskEntity.title = updatedTask.title ?: savedTaskEntity.title
        savedTaskEntity.description = updatedTask.description ?: savedTaskEntity.description
        savedTaskEntity.priority = updatedTask.priority ?: savedTaskEntity.priority
        savedTaskEntity.assignedTo = updatedTask.assignedTo?.value ?: savedTaskEntity.assignedTo

        return catchingPersistenceErrors(
            conflictMessage = "Task could not be updated due to a data conflict.",
            unexpectedErrorMessage = "Unexpected error while updating task.",
        ) {
            springDataTaskRepository.saveAndFlush(savedTaskEntity)
                .toDomainResult("Could not map updated task to domain model.")
        }
    }

    override fun transition(taskId: TaskId, targetStatus: TaskStatus, expectedVersion: Long): RepositoryResult<Task> {
        val savedTaskEntity = springDataTaskRepository.findById(taskId.value).getOrNull()
            ?: return RepositoryResult.NotFound

        savedTaskEntity.checkVersion(expectedVersion)?.let { return it }

        savedTaskEntity.status = targetStatus

        return catchingPersistenceErrors(
            conflictMessage = "Task could not be transitioned due to a data conflict.",
            unexpectedErrorMessage = "Unexpected error while transitioning task.",
        ) {
            springDataTaskRepository.saveAndFlush(savedTaskEntity)
                .toDomainResult("Could not map transitioned task to domain model.")
        }
    }

    override fun delete(id: TaskId): RepositoryResult<Unit> {
        if (!springDataTaskRepository.existsById(id.value)) {
            return RepositoryResult.NotFound
        }

        return catchingPersistenceErrors(
            conflictMessage = "Task could not be deleted due to a data conflict.",
            unexpectedErrorMessage = "Unexpected error while deleting task.",
        ) {
            springDataTaskRepository.deleteById(id.value)
            // Force the DELETE to execute now, inside this try/catch: without an explicit flush,
            // Hibernate defers it to transaction commit, where a foreign key violation (the task
            // still has audit log entries) would escape as an unhandled 500 instead of a 409.
            springDataTaskRepository.flush()
            RepositoryResult.Ok(Unit)
        }
    }
}

private fun TaskEntity.toDomainResult(errorMessage: String): RepositoryResult<Task> = try {
    RepositoryResult.Ok(toDomain())
} catch (e: IllegalArgumentException) {
    markRollbackOnly()
    RepositoryResult.UnexpectedError(errorMessage, e)
}

private fun Page<TaskEntity>.toDomainResult(errorMessage: String): RepositoryResult<Page<Task>> = try {
    RepositoryResult.Ok(map { it.toDomain() })
} catch (e: IllegalArgumentException) {
    markRollbackOnly()
    RepositoryResult.UnexpectedError(errorMessage, e)
}

private fun TaskEntity.checkVersion(expectedVersion: Long): RepositoryResult.VersionConflict? =
    if (version != expectedVersion) {
        RepositoryResult.VersionConflict(
            "Task was modified concurrently: expected version $expectedVersion but was $version.",
            null,
        )
    } else {
        null
    }
