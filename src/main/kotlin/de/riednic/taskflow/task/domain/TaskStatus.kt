package de.riednic.taskflow.task.domain

enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    IN_REVIEW,
    DONE,
    CANCELLED,
    REJECTED
}
