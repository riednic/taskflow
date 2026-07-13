package de.riednic.taskflow.task.controller

import com.fasterxml.jackson.annotation.JsonProperty
import de.riednic.taskflow.task.domain.Task
import de.riednic.taskflow.task.domain.TaskPriority
import de.riednic.taskflow.task.domain.TaskStatus
import kotlin.time.Instant

data class TaskResponse(

    @JsonProperty("id")
    val id: Long,

    @JsonProperty("title")
    val title: String,

    @JsonProperty("description")
    val description: String?,

    @JsonProperty("status")
    val status: TaskStatus,

    @JsonProperty("priority")
    val priority: TaskPriority,

    @JsonProperty("assignedTo")
    val assignedTo: Long?,

    @JsonProperty("version")
    val version: Long,

    @JsonProperty("createdAt")
    val createdAt: Instant,

    @JsonProperty("updatedAt")
    val updatedAt: Instant
)

fun Task.toResponse() = TaskResponse(
    id = id.value,
    title = title,
    description = description,
    status = status,
    priority = priority,
    assignedTo = assignedTo?.value,
    version = version,
    createdAt = createdAt,
    updatedAt = updatedAt
)
