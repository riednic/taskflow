package de.riednic.taskflow.task.persistence

import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.common.persistence.catchingPersistenceErrors
import de.riednic.taskflow.task.application.TaskAuditLogRepository
import de.riednic.taskflow.task.domain.TaskId
import de.riednic.taskflow.task.domain.TaskStatus
import de.riednic.taskflow.user.domain.UserId
import org.springframework.stereotype.Repository

@Repository
class TaskAuditLogRepositoryImpl(
    private val springDataTaskStatusAuditLogRepository: SpringDataTaskStatusAuditLogRepository,
) : TaskAuditLogRepository {

    override fun record(
        taskId: TaskId,
        from: TaskStatus,
        to: TaskStatus,
        changedBy: UserId,
    ): RepositoryResult<Unit> =
        catchingPersistenceErrors(
            conflictMessage = "Task status transition could not be recorded due to a data conflict.",
            unexpectedErrorMessage = "Unexpected error while recording task status transition.",
        ) {
            springDataTaskStatusAuditLogRepository.saveAndFlush(
                TaskStatusAuditLogEntity(
                    taskId = taskId.value,
                    fromStatus = from,
                    toStatus = to,
                    changedBy = changedBy.value,
                )
            )
            RepositoryResult.Ok(Unit)
        }
}
