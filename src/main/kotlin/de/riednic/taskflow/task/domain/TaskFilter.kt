package de.riednic.taskflow.task.domain

import de.riednic.taskflow.user.domain.UserId

data class TaskFilter(
    val status: TaskStatus? = null,
    val priority: TaskPriority? = null,
    val assignedTo: UserId? = null,
)
