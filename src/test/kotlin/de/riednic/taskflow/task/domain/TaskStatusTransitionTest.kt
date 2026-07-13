package de.riednic.taskflow.task.domain

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TaskStatusTransitionTest {

    @ParameterizedTest
    @CsvSource(
        "TODO, IN_PROGRESS",
        "TODO, CANCELLED",
        "IN_PROGRESS, IN_REVIEW",
        "IN_PROGRESS, CANCELLED",
        "IN_REVIEW, DONE",
        "IN_REVIEW, REJECTED",
        "IN_REVIEW, CANCELLED",
        "REJECTED, IN_PROGRESS",
    )
    fun `allows the transitions defined by the workflow`(from: TaskStatus, to: TaskStatus) {
        assertTrue(from.isValidTransitionTo(to))
    }

    @ParameterizedTest
    @CsvSource(
        "TODO, DONE",
        "TODO, IN_REVIEW",
        "TODO, REJECTED",
        "IN_PROGRESS, DONE",
        "IN_PROGRESS, REJECTED",
        "IN_PROGRESS, TODO",
        "IN_REVIEW, IN_PROGRESS",
        "IN_REVIEW, TODO",
        "REJECTED, DONE",
        "REJECTED, IN_REVIEW",
        "REJECTED, CANCELLED",
        "DONE, TODO",
        "DONE, IN_PROGRESS",
        "CANCELLED, TODO",
        "CANCELLED, IN_PROGRESS",
    )
    fun `rejects transitions not defined by the workflow`(from: TaskStatus, to: TaskStatus) {
        assertFalse(from.isValidTransitionTo(to))
    }

    @ParameterizedTest
    @CsvSource(
        "DONE, DONE",
        "DONE, CANCELLED",
        "CANCELLED, DONE",
        "CANCELLED, CANCELLED",
    )
    fun `treats DONE and CANCELLED as terminal states`(from: TaskStatus, to: TaskStatus) {
        assertFalse(from.isValidTransitionTo(to))
    }
}
