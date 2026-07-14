package de.riednic.taskflow.statistics.domain

import de.riednic.taskflow.task.domain.TaskStatus
import de.riednic.taskflow.user.domain.UserId

data class TaskStatistics(
    val countsByStatus: Map<TaskStatus, Long>,
    val averageTodoToDoneHours: Double?,
    val topUsersByCompletedTasks: List<UserCompletedTasks>,
)

data class UserCompletedTasks(
    val userId: UserId,
    val name: String,
    val completedTasks: Long,
)
