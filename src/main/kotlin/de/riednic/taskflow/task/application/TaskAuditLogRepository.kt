package de.riednic.taskflow.task.application

import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.task.domain.TaskStatus

interface TaskAuditLogRepository {

    fun record(taskId: Long, from: TaskStatus, to: TaskStatus, changedBy: Long): RepositoryResult<Unit>
}
