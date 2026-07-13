package de.riednic.taskflow.task.controller

import com.fasterxml.jackson.annotation.JsonProperty
import de.riednic.taskflow.task.domain.TaskPriority
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class ReplaceTaskRequest(

    @NotBlank
    @Size(max = 255)
    @JsonProperty("title")
    val title: String,

    @Size(max = 1000)
    @JsonProperty("description")
    val description: String? = null,

    @NotNull
    @JsonProperty("priority")
    val priority: TaskPriority,

    @JsonProperty("assignedTo")
    val assignedTo: Long? = null,

    @NotNull
    @JsonProperty("version")
    val version: Long,
)
