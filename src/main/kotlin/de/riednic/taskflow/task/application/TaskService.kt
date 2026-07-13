package de.riednic.taskflow.task.application

import de.riednic.taskflow.common.application.ServiceResult
import de.riednic.taskflow.common.application.toServiceError
import de.riednic.taskflow.common.application.toServiceResult
import de.riednic.taskflow.common.persistence.RepositoryResult
import de.riednic.taskflow.task.controller.CreateTaskRequest
import de.riednic.taskflow.task.controller.ReplaceTaskRequest
import de.riednic.taskflow.task.controller.TransitionTaskStatusRequest
import de.riednic.taskflow.task.controller.UpdateTaskRequest
import de.riednic.taskflow.task.domain.NewTask
import de.riednic.taskflow.task.domain.ReplacementTask
import de.riednic.taskflow.task.domain.Task
import de.riednic.taskflow.task.domain.TaskEnteredReviewEvent
import de.riednic.taskflow.task.domain.TaskFilter
import de.riednic.taskflow.task.domain.TaskId
import de.riednic.taskflow.task.domain.TaskStatus
import de.riednic.taskflow.task.domain.TaskStatusTransitionPolicy
import de.riednic.taskflow.task.domain.UpdatedTask
import de.riednic.taskflow.task.domain.isValidTransitionTo
import de.riednic.taskflow.user.domain.UserId
import de.riednic.taskflow.user.domain.UserRole
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TaskService(
    private val taskRepository: TaskRepository,
    private val taskAuditLogRepository: TaskAuditLogRepository,
    private val eventPublisher: ApplicationEventPublisher,
) {

    @Transactional(readOnly = true)
    fun getTasks(filter: TaskFilter, pageable: Pageable): ServiceResult<Page<Task>> {
        return taskRepository.findAll(filter, pageable).toServiceResult()
    }

    @Transactional(readOnly = true)
    fun getTaskById(taskId: TaskId): ServiceResult<Task> {
        return taskRepository.findById(taskId).toServiceResult()
    }

    fun createTask(request: CreateTaskRequest): ServiceResult<Task> {
        val newTask = try {
            NewTask(
                title = request.title,
                description = request.description,
                status = request.status,
                priority = request.priority,
                assignedTo = request.assignedTo?.let { UserId(it) },
            )
        } catch (e: IllegalArgumentException) {
            return ServiceResult.ValidationError(e.message ?: "Invalid task data.")
        }

        return taskRepository.save(newTask).toServiceResult()
    }

    fun replaceTask(taskId: TaskId, request: ReplaceTaskRequest): ServiceResult<Task> {
        val replacementTask = try {
            ReplacementTask(
                id = taskId,
                title = request.title,
                description = request.description,
                priority = request.priority,
                assignedTo = request.assignedTo?.let { UserId(it) },
                version = request.version,
            )
        } catch (e: IllegalArgumentException) {
            return ServiceResult.ValidationError(e.message ?: "Invalid task data.")
        }

        return taskRepository.replace(replacementTask).toServiceResult()
    }

    fun updateTask(taskId: TaskId, request: UpdateTaskRequest): ServiceResult<Task> {
        val updatedTask = try {
            UpdatedTask(
                id = taskId,
                title = request.title,
                description = request.description,
                priority = request.priority,
                assignedTo = request.assignedTo?.let { UserId(it) },
                version = request.version,
            )
        } catch (e: IllegalArgumentException) {
            return ServiceResult.ValidationError(e.message ?: "Invalid task data.")
        }

        return taskRepository.update(updatedTask).toServiceResult()
    }

    fun deleteTask(taskId: TaskId): ServiceResult<Unit> {
        return taskRepository.delete(taskId).toServiceResult()
    }

    fun transitionTaskStatus(
        taskId: TaskId,
        request: TransitionTaskStatusRequest,
        requestingUserId: UserId,
        requestingRole: UserRole,
    ): ServiceResult<Task> {
        val savedTask = when (val current = taskRepository.findById(taskId)) {
            is RepositoryResult.Error -> return current.toServiceError()
            is RepositoryResult.Success -> current.value
        }

        if (!TaskStatusTransitionPolicy.isAllowed(requestingRole, requestingUserId, savedTask, request.status)) {
            return ServiceResult.Forbidden(
                "User $requestingUserId is not allowed to transition task $taskId to ${request.status}."
            )
        }
        if (!savedTask.status.isValidTransitionTo(request.status)) {
            return InvalidTransitionError(
                "Transition from ${savedTask.status} to ${request.status} is not allowed."
            )
        }

        val transitionResult = taskRepository.transition(taskId, request.status, request.version)
        val transitionedTask = when (transitionResult) {
            is RepositoryResult.Error -> return transitionResult.toServiceError()
            is RepositoryResult.Success -> transitionResult.value
        }

        val auditResult = taskAuditLogRepository.record(taskId, savedTask.status, request.status, requestingUserId)
        if (auditResult is RepositoryResult.Error) {
            return auditResult.toServiceError()
        }

        if (request.status == TaskStatus.IN_REVIEW) {
            eventPublisher.publishEvent(TaskEnteredReviewEvent(taskId))
        }

        return ServiceResult.Ok(transitionedTask)
    }
}
