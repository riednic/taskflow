package de.riednic.taskflow.statistics.persistence

import de.riednic.taskflow.task.domain.TaskStatus
import org.springframework.beans.factory.annotation.Value

interface StatusCountProjection {
    val status: TaskStatus
    val count: Long
}

interface TopUserCompletedTasksProjection {

    @get:Value("#{target.user_id}")
    val userId: Long

    val name: String

    @get:Value("#{target.completed_tasks}")
    val completedTasks: Long
}
