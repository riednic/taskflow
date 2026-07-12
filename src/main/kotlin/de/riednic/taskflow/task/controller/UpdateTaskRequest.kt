package de.riednic.taskflow.task.controller

import com.fasterxml.jackson.annotation.JsonProperty
import de.riednic.taskflow.task.domain.TaskPriority
import de.riednic.taskflow.task.domain.TaskStatus
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpdateTaskRequest(

    @Size(max = 255)
    @JsonProperty("title")
    val title: String? = null,

    @Size(max = 1000)
    @JsonProperty("description")
    val description: String? = null,

    @JsonProperty("status")
    val status: TaskStatus? = null,

    @JsonProperty("priority")
    val priority: TaskPriority? = null,

    @JsonProperty("assignedTo")
    val assignedTo: Long? = null,

    @NotNull
    @JsonProperty("version")
    val version: Long,
)
