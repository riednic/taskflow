package de.riednic.taskflow.task.domain

data class TaskFilter(
    val status: TaskStatus? = null,
    val priority: TaskPriority? = null,
    val assignedTo: Long? = null,
)
