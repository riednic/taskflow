package de.riednic.taskflow.task.controller

import com.fasterxml.jackson.annotation.JsonProperty
import de.riednic.taskflow.task.domain.TaskPriority
import de.riednic.taskflow.task.domain.TaskStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class CreateTaskRequest(

    @NotBlank
    @Size(max = 255)
    @JsonProperty("title")
    val title: String,

    @Size(max = 1000)
    @JsonProperty("description")
    val description: String? = null,

    @NotNull
    @JsonProperty("status")
    val status: TaskStatus,

    @NotNull
    @JsonProperty("priority")
    val priority: TaskPriority,

    @JsonProperty("assignedTo")
    val assignedTo: Long? = null,
)
