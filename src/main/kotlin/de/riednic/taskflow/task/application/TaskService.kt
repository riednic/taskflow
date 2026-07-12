package de.riednic.taskflow.task.application

import de.riednic.taskflow.common.ServiceResult
import de.riednic.taskflow.common.toServiceResult
import de.riednic.taskflow.task.controller.CreateTaskRequest
import de.riednic.taskflow.task.controller.ReplaceTaskRequest
import de.riednic.taskflow.task.controller.UpdateTaskRequest
import de.riednic.taskflow.task.domain.NewTask
import de.riednic.taskflow.task.domain.ReplacementTask
import de.riednic.taskflow.task.domain.Task
import de.riednic.taskflow.task.domain.TaskFilter
import de.riednic.taskflow.task.domain.UpdatedTask
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class TaskService(
    private val taskRepository: TaskRepository
) {

    fun getTasks(filter: TaskFilter, pageable: Pageable): ServiceResult<Page<Task>> {
        return taskRepository.findAll(filter, pageable).toServiceResult()
    }

    fun getTaskById(id: Long): ServiceResult<Task> {
        return taskRepository.findById(id).toServiceResult()
    }

    fun createTask(request: CreateTaskRequest): ServiceResult<Task> {
        val newTask = try {
            NewTask(
                title = request.title,
                description = request.description,
                status = request.status,
                priority = request.priority,
                assignedTo = request.assignedTo,
            )
        } catch (e: IllegalArgumentException) {
            return ServiceResult.ValidationError(e.message ?: "Invalid task data.")
        }

        return taskRepository.save(newTask).toServiceResult()
    }

    fun replaceTask(id: Long, request: ReplaceTaskRequest): ServiceResult<Task> {
        val replacementTask = try {
            ReplacementTask(
                id = id,
                title = request.title,
                description = request.description,
                status = request.status,
                priority = request.priority,
                assignedTo = request.assignedTo,
                version = request.version,
            )
        } catch (e: IllegalArgumentException) {
            return ServiceResult.ValidationError(e.message ?: "Invalid task data.")
        }

        return taskRepository.replace(replacementTask).toServiceResult()
    }

    fun updateTask(id: Long, request: UpdateTaskRequest): ServiceResult<Task> {
        val updatedTask = try {
            UpdatedTask(
                id = id,
                title = request.title,
                description = request.description,
                status = request.status,
                priority = request.priority,
                assignedTo = request.assignedTo,
                version = request.version,
            )
        } catch (e: IllegalArgumentException) {
            return ServiceResult.ValidationError(e.message ?: "Invalid task data.")
        }

        return taskRepository.update(updatedTask).toServiceResult()
    }

    fun deleteTask(id: Long): ServiceResult<Nothing> {
        return taskRepository.delete(id).toServiceResult()
    }
}
