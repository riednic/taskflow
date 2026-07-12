package de.riednic.taskflow.task.controller

import com.fasterxml.jackson.annotation.JsonProperty
import de.riednic.taskflow.task.domain.TaskStatus
import jakarta.validation.constraints.NotNull

data class TransitionTaskStatusRequest(

    @NotNull
    @JsonProperty("status")
    val status: TaskStatus,

    @NotNull
    @JsonProperty("version")
    val version: Long,
)
