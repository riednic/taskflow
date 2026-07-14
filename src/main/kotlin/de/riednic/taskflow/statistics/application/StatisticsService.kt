package de.riednic.taskflow.statistics.application

import de.riednic.taskflow.common.application.ServiceResult
import de.riednic.taskflow.common.application.toServiceError
import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.statistics.domain.TaskStatistics
import de.riednic.taskflow.task.domain.TaskStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private const val TOP_USERS_LIMIT = 3

@Service
@Transactional(readOnly = true)
class StatisticsService(
    private val statisticsRepository: StatisticsRepository,
) {

    fun getTaskStatistics(): ServiceResult<TaskStatistics> {
        val countsByStatus = when (val result = statisticsRepository.countTasksByStatus()) {
            is RepositoryResult.Error -> return result.toServiceError()
            is RepositoryResult.Success -> result.value
        }

        val averageTodoToDoneHours = when (val result = statisticsRepository.averageTodoToDoneHours()) {
            is RepositoryResult.Error -> return result.toServiceError()
            is RepositoryResult.Success -> result.value
        }

        val topUsers = when (val result = statisticsRepository.topUsersByCompletedTasks(TOP_USERS_LIMIT)) {
            is RepositoryResult.Error -> return result.toServiceError()
            is RepositoryResult.Success -> result.value
        }

        return ServiceResult.Ok(
            TaskStatistics(
                countsByStatus = TaskStatus.entries.associateWith { countsByStatus[it] ?: 0L },
                averageTodoToDoneHours = averageTodoToDoneHours,
                topUsersByCompletedTasks = topUsers,
            )
        )
    }
}
