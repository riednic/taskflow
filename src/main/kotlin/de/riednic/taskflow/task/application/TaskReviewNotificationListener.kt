package de.riednic.taskflow.task.application

import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.task.domain.TaskEnteredReviewEvent
import de.riednic.taskflow.user.application.UserRepository
import de.riednic.taskflow.user.domain.UserRole
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class TaskReviewNotificationListener(
    private val userRepository: UserRepository,
) {

    private val log = LoggerFactory.getLogger(TaskReviewNotificationListener::class.java)

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun onTaskEnteredReview(event: TaskEnteredReviewEvent) {
        val reviewers = userRepository.findAllByRole(UserRole.REVIEWER)
        if (reviewers !is RepositoryResult.Success) {
            log.warn("Could not load reviewers to notify about task {} entering review.", event.taskId)
            return
        }

        reviewers.value.forEach { reviewer ->
            log.info("Notifying reviewer {} that task {} is ready for review.", reviewer.id, event.taskId)
        }
    }
}
