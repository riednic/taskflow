package de.riednic.taskflow.statistics.persistence

import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.common.persistence.catchingPersistenceErrors
import de.riednic.taskflow.statistics.application.StatisticsRepository
import de.riednic.taskflow.statistics.domain.UserCompletedTasks
import de.riednic.taskflow.task.domain.TaskStatus
import de.riednic.taskflow.user.domain.UserId
import org.springframework.stereotype.Repository

@Repository
class StatisticsRepositoryImpl(
    private val springDataStatisticsRepository: SpringDataStatisticsRepository,
) : StatisticsRepository {

    override fun countTasksByStatus(): RepositoryResult<Map<TaskStatus, Long>> = catchingPersistenceErrors(
        conflictMessage = "Task counts could not be read due to a data conflict.",
        unexpectedErrorMessage = "Unexpected error while reading task counts by status.",
    ) {
        val counts = springDataStatisticsRepository.countTasksByStatus()
            .associate { it.status to it.count }
        RepositoryResult.Ok(counts)
    }

    override fun averageTodoToDoneHours(): RepositoryResult<Double?> = catchingPersistenceErrors(
        conflictMessage = "Average completion time could not be read due to a data conflict.",
        unexpectedErrorMessage = "Unexpected error while reading average completion time.",
    ) {
        RepositoryResult.Ok(springDataStatisticsRepository.averageTodoToDoneHours())
    }

    override fun topUsersByCompletedTasks(limit: Int): RepositoryResult<List<UserCompletedTasks>> =
        catchingPersistenceErrors(
            conflictMessage = "Top users by completed tasks could not be read due to a data conflict.",
            unexpectedErrorMessage = "Unexpected error while reading top users by completed tasks.",
        ) {
            val topUsers = springDataStatisticsRepository.topUsersByCompletedTasks(limit).map {
                UserCompletedTasks(userId = UserId(it.userId), name = it.name, completedTasks = it.completedTasks)
            }
            RepositoryResult.Ok(topUsers)
        }
}
