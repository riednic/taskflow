package de.riednic.taskflow.task.application

import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.task.domain.TaskId
import de.riednic.taskflow.task.domain.TaskStatus
import de.riednic.taskflow.user.domain.UserId

interface TaskAuditLogRepository {

    fun record(taskId: TaskId, from: TaskStatus, to: TaskStatus, changedBy: UserId): RepositoryResult<Unit>
}
