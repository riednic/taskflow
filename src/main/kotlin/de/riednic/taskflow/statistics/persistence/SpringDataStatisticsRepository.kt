package de.riednic.taskflow.statistics.persistence

import de.riednic.taskflow.task.persistence.TaskEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.Repository
import org.springframework.data.repository.query.Param

interface SpringDataStatisticsRepository : Repository<TaskEntity, Long> {

    @Query(
        """
        SELECT t.status AS status, COUNT(t) AS count
        FROM TaskEntity t
        GROUP BY t.status
        """
    )
    fun countTasksByStatus(): List<StatusCountProjection>

    // Done is a terminal state, so no further updates are possible at this point.
    @Query(
        value = """
            SELECT AVG(EXTRACT(EPOCH FROM (updated_at - created_at)) / 3600.0)
            FROM tasks
            WHERE status = 'DONE'
        """,
        nativeQuery = true,
    )
    fun averageTodoToDoneHours(): Double?

    @Query(
        value = """
            SELECT u.id AS user_id, u.name AS name, COUNT(*) AS completed_tasks
            FROM tasks t
            JOIN users u ON u.id = t.assigned_to
            WHERE t.status = 'DONE'
            GROUP BY u.id, u.name
            ORDER BY COUNT(*) DESC
            LIMIT :limit
        """,
        nativeQuery = true,
    )
    fun topUsersByCompletedTasks(@Param("limit") limit: Int): List<TopUserCompletedTasksProjection>
}
