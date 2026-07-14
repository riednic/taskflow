package de.riednic.taskflow.statistics.application

import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.statistics.domain.UserCompletedTasks
import de.riednic.taskflow.task.domain.TaskStatus

interface StatisticsRepository {

    fun countTasksByStatus(): RepositoryResult<Map<TaskStatus, Long>>
    fun averageTodoToDoneHours(): RepositoryResult<Double?>
    fun topUsersByCompletedTasks(limit: Int): RepositoryResult<List<UserCompletedTasks>>
}
