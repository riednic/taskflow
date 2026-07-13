package de.riednic.taskflow.task.domain

import de.riednic.taskflow.user.domain.UserId
import de.riednic.taskflow.user.domain.UserRole
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.toKotlinInstant

class TaskTransitionPolicyTest {

    private val assignedUserId = UserId(1L)
    private val otherUserId = UserId(2L)
    private val now = java.time.Instant.now().toKotlinInstant()

    private fun task(assignedTo: UserId?) = Task(
        id = TaskId(1),
        title = "Task",
        description = null,
        status = TaskStatus.TODO,
        priority = TaskPriority.LOW,
        assignedTo = assignedTo,
        version = 0,
        createdAt = now,
        updatedAt = now,
    )

    @Test
    fun `assigned user may set task to IN_PROGRESS`() {
        assertTrue(
            TaskStatusTransitionPolicy.isAllowed(
                UserRole.MEMBER, assignedUserId, task(assignedTo = assignedUserId), TaskStatus.IN_PROGRESS
            )
        )
    }

    @Test
    fun `admin may set any task to IN_PROGRESS`() {
        assertTrue(
            TaskStatusTransitionPolicy.isAllowed(
                UserRole.ADMIN, otherUserId, task(assignedTo = assignedUserId), TaskStatus.IN_PROGRESS
            )
        )
    }

    @Test
    fun `unassigned member may not set task to IN_PROGRESS`() {
        assertFalse(
            TaskStatusTransitionPolicy.isAllowed(
                UserRole.MEMBER, otherUserId, task(assignedTo = assignedUserId), TaskStatus.IN_PROGRESS
            )
        )
    }

    @Test
    fun `assigned member may set task to IN_REVIEW or CANCELLED`() {
        val assignedTask = task(assignedTo = assignedUserId)
        assertTrue(TaskStatusTransitionPolicy.isAllowed(UserRole.MEMBER, assignedUserId, assignedTask, TaskStatus.IN_REVIEW))
        assertTrue(TaskStatusTransitionPolicy.isAllowed(UserRole.MEMBER, assignedUserId, assignedTask, TaskStatus.CANCELLED))
    }

    @Test
    fun `unassigned reviewer may not set task to IN_REVIEW or CANCELLED`() {
        val assignedTask = task(assignedTo = assignedUserId)
        assertFalse(TaskStatusTransitionPolicy.isAllowed(UserRole.REVIEWER, otherUserId, assignedTask, TaskStatus.IN_REVIEW))
        assertFalse(TaskStatusTransitionPolicy.isAllowed(UserRole.REVIEWER, otherUserId, assignedTask, TaskStatus.CANCELLED))
    }

    @Test
    fun `only reviewer or admin may set task to DONE or REJECTED`() {
        val assignedTask = task(assignedTo = assignedUserId)
        assertTrue(TaskStatusTransitionPolicy.isAllowed(UserRole.REVIEWER, otherUserId, assignedTask, TaskStatus.DONE))
        assertTrue(TaskStatusTransitionPolicy.isAllowed(UserRole.ADMIN, otherUserId, assignedTask, TaskStatus.REJECTED))
        assertFalse(TaskStatusTransitionPolicy.isAllowed(UserRole.MEMBER, assignedUserId, assignedTask, TaskStatus.DONE))
    }

    @Test
    fun `no role may transition a task back to TODO`() {
        val assignedTask = task(assignedTo = assignedUserId)
        assertFalse(TaskStatusTransitionPolicy.isAllowed(UserRole.ADMIN, assignedUserId, assignedTask, TaskStatus.TODO))
    }
}
