package de.riednic.taskflow.task.domain

import kotlin.time.Instant

data class Task(
    val id: Long,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val priority: TaskPriority,
    val assignedTo: Long?,
    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    init {
        require(id > 0) { "id must be positive, was $id" }
        require(title.isNotBlank()) { "title must not be blank" }
        require(title.length <= MAX_TITLE_LENGTH) { "title must not exceed $MAX_TITLE_LENGTH characters" }
        require(description == null || description.length <= MAX_DESCRIPTION_LENGTH) {
            "description must not exceed $MAX_DESCRIPTION_LENGTH characters"
        }
        require(assignedTo == null || assignedTo > 0) { "assignedTo must be a positive user id, was $assignedTo" }
        require(version >= 0) { "version must not be negative, was $version" }
    }

    companion object {
        const val MAX_TITLE_LENGTH = 255
        const val MAX_DESCRIPTION_LENGTH = 1000
    }
}
