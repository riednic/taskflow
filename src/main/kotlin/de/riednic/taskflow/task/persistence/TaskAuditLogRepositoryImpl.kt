package de.riednic.taskflow.task.persistence

import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.common.persistence.catchingPersistenceErrors
import de.riednic.taskflow.task.application.TaskAuditLogRepository
import de.riednic.taskflow.task.domain.TaskStatus
import org.springframework.stereotype.Repository

@Repository
class TaskAuditLogRepositoryImpl(
    private val springDataTaskStatusAuditLogRepository: SpringDataTaskStatusAuditLogRepository,
) : TaskAuditLogRepository {

    override fun record(taskId: Long, from: TaskStatus, to: TaskStatus, changedBy: Long): RepositoryResult<Unit> =
        catchingPersistenceErrors(
            conflictMessage = "Task status transition could not be recorded due to a data conflict.",
            unexpectedErrorMessage = "Unexpected error while recording task status transition.",
        ) {
            springDataTaskStatusAuditLogRepository.saveAndFlush(
                TaskStatusAuditLogEntity(
                    taskId = taskId,
                    fromStatus = from,
                    toStatus = to,
                    changedBy = changedBy,
                )
            )
            RepositoryResult.Ok(Unit)
        }
}
