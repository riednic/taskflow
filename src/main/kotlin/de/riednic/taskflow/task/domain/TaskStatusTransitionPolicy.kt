package de.riednic.taskflow.task.domain

import de.riednic.taskflow.user.domain.UserId
import de.riednic.taskflow.user.domain.UserRole

private val possibleTransitions = mapOf(
    TaskStatus.TODO to setOf(TaskStatus.IN_PROGRESS, TaskStatus.CANCELLED),
    TaskStatus.IN_PROGRESS to setOf(TaskStatus.IN_REVIEW, TaskStatus.CANCELLED),
    TaskStatus.IN_REVIEW to setOf(TaskStatus.DONE, TaskStatus.REJECTED, TaskStatus.CANCELLED),
    TaskStatus.REJECTED to setOf(TaskStatus.IN_PROGRESS),
    TaskStatus.DONE to emptySet(),
    TaskStatus.CANCELLED to emptySet(),
)

fun TaskStatus.isValidTransitionTo(target: TaskStatus): Boolean =
    target in (possibleTransitions[this] ?: emptySet())

val TaskStatus.isTerminal: Boolean
    get() = possibleTransitions[this]?.isEmpty() ?: true

object TaskStatusTransitionPolicy {

    fun isAllowed(role: UserRole, requestingUserId: UserId, task: Task, target: TaskStatus): Boolean {
        val isAssignedOrAdmin = role == UserRole.ADMIN || task.assignedTo == requestingUserId
        return when (target) {
            TaskStatus.IN_PROGRESS, TaskStatus.IN_REVIEW, TaskStatus.CANCELLED -> isAssignedOrAdmin
            TaskStatus.DONE, TaskStatus.REJECTED -> role == UserRole.ADMIN || role == UserRole.REVIEWER
            TaskStatus.TODO -> false
        }
    }
}
