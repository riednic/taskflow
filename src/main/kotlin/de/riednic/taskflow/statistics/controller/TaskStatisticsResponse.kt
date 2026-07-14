package de.riednic.taskflow.statistics.controller

import com.fasterxml.jackson.annotation.JsonProperty
import de.riednic.taskflow.statistics.domain.TaskStatistics
import de.riednic.taskflow.statistics.domain.UserCompletedTasks
import de.riednic.taskflow.task.domain.TaskStatus

data class TaskStatisticsResponse(

    @JsonProperty("countsByStatus")
    val countsByStatus: Map<TaskStatus, Long>,

    @JsonProperty("averageTodoToDoneHours")
    val averageTodoToDoneHours: Double?,

    @JsonProperty("topUsersByCompletedTasks")
    val topUsersByCompletedTasks: List<TopUserStatisticResponse>,
)

data class TopUserStatisticResponse(

    @JsonProperty("userId")
    val userId: Long,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("completedTasks")
    val completedTasks: Long,
)

fun TaskStatistics.toResponse() = TaskStatisticsResponse(
    countsByStatus = countsByStatus,
    averageTodoToDoneHours = averageTodoToDoneHours,
    topUsersByCompletedTasks = topUsersByCompletedTasks.map { it.toResponse() },
)

fun UserCompletedTasks.toResponse() = TopUserStatisticResponse(
    userId = userId.value,
    name = name,
    completedTasks = completedTasks,
)
