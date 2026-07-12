package de.riednic.taskflow.task.application

import de.riednic.taskflow.common.RepositoryResult
import de.riednic.taskflow.task.domain.NewTask
import de.riednic.taskflow.task.domain.ReplacementTask
import de.riednic.taskflow.task.domain.Task
import de.riednic.taskflow.task.domain.TaskFilter
import de.riednic.taskflow.task.domain.UpdatedTask
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface TaskRepository {

    fun findById(id: Long): RepositoryResult<Task>
    fun findAll(filter: TaskFilter, pageable: Pageable): RepositoryResult<Page<Task>>
    fun save(newTask: NewTask): RepositoryResult<Task>
    fun replace(replacementTask: ReplacementTask): RepositoryResult<Task>
    fun update(updatedTask: UpdatedTask): RepositoryResult<Task>
    fun delete(id: Long): RepositoryResult<Nothing>
}
