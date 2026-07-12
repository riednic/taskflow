package de.riednic.taskflow.task.domain

data class NewTask(
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val priority: TaskPriority,
    val assignedTo: Long? = null,
) {
    init {
        require(title.isNotBlank()) { "title must not be blank" }
        require(title.length <= Task.MAX_TITLE_LENGTH) { "title must not exceed ${Task.MAX_TITLE_LENGTH} characters" }
        require(description == null || description.length <= Task.MAX_DESCRIPTION_LENGTH) {
            "description must not exceed ${Task.MAX_DESCRIPTION_LENGTH} characters"
        }
        require(assignedTo == null || assignedTo > 0) { "assignedTo must be a positive user id, was $assignedTo" }
    }
}
