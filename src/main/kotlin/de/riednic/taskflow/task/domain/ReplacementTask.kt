package de.riednic.taskflow.task.domain

import de.riednic.taskflow.user.domain.UserId

data class ReplacementTask(
    val id: TaskId,
    val title: String,
    val description: String?,
    val priority: TaskPriority,
    val assignedTo: UserId? = null,
    val version: Long,
) {
    init {
        require(id.value > 0) { "id must be positive, was $id" }
        require(title.isNotBlank()) { "title must not be blank" }
        require(title.length <= Task.MAX_TITLE_LENGTH) { "title must not exceed ${Task.MAX_TITLE_LENGTH} characters" }
        require(description == null || description.length <= Task.MAX_DESCRIPTION_LENGTH) {
            "description must not exceed ${Task.MAX_DESCRIPTION_LENGTH} characters"
        }
        require(assignedTo == null || assignedTo.value > 0) { "assignedTo must be a positive user id, was $assignedTo" }
        require(version >= 0) { "version must not be negative, was $version" }
    }
}
