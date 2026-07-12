package de.riednic.taskflow.task.controller

import de.riednic.taskflow.auth.application.AuthUser
import de.riednic.taskflow.common.ServiceResult
import de.riednic.taskflow.task.application.TaskService
import de.riednic.taskflow.task.domain.TaskFilter
import de.riednic.taskflow.task.domain.TaskPriority
import de.riednic.taskflow.task.domain.TaskStatus
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ProblemDetail
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder

@Validated
@RestController
@RequestMapping("api/v1/tasks")
class TaskController(
    private val taskService: TaskService,
) {

    @GetMapping
    fun getTasks(
        @RequestParam(required = false) status: TaskStatus?,
        @RequestParam(required = false) priority: TaskPriority?,
        @RequestParam(required = false) assignedTo: Long?,
        @PageableDefault(size = 10) pageable: Pageable,
    ): ResponseEntity<Any> {
        val filter = TaskFilter(status = status, priority = priority, assignedTo = assignedTo)
        return when (val result = taskService.getTasks(filter, pageable)) {
            is ServiceResult.Success -> ResponseEntity.ok(result.value.map { it.toResponse() })
            else -> result.toErrorResponse()
        }
    }

    @GetMapping("/{id}")
    fun getTaskById(@PathVariable id: Long): ResponseEntity<Any> {
        return when (val result = taskService.getTaskById(id)) {
            is ServiceResult.Success -> ResponseEntity.ok(result.value.toResponse())
            else -> result.toErrorResponse()
        }
    }

    @PostMapping
    fun createTask(
        @Valid @RequestBody request: CreateTaskRequest,
        uriComponentsBuilder: UriComponentsBuilder,
    ): ResponseEntity<Any> {
        return when (val result = taskService.createTask(request)) {
            is ServiceResult.Success -> {
                val task = result.value.toResponse()
                val location = uriComponentsBuilder.path("/tasks/{id}").buildAndExpand(task.id).toUri()
                ResponseEntity.created(location).body(task)
            }

            else -> result.toErrorResponse()
        }
    }

    @PutMapping("/{id}")
    fun replaceTask(
        @PathVariable id: Long,
        @Valid @RequestBody request: ReplaceTaskRequest,
    ): ResponseEntity<Any> {
        return when (val result = taskService.replaceTask(id, request)) {
            is ServiceResult.Success -> ResponseEntity.ok(result.value.toResponse())
            else -> result.toErrorResponse()
        }
    }

    // TODO: description = null / assignedTo = null currently not supported, will lead to the db
    // not setting the value to null currently
    @PatchMapping("/{id}")
    fun updateTask(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateTaskRequest,
    ): ResponseEntity<Any> {
        return when (val result = taskService.updateTask(id, request)) {
            is ServiceResult.Success -> ResponseEntity.ok(result.value.toResponse())
            else -> result.toErrorResponse()
        }
    }

    @PostMapping("/{id}/status")
    fun transitionTaskStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: TransitionTaskStatusRequest,
        @AuthenticationPrincipal authUser: AuthUser,
    ): ResponseEntity<Any> {
        return when (val result = taskService.transitionTaskStatus(id, request, authUser.id, authUser.role)) {
            is ServiceResult.Success -> ResponseEntity.ok(result.value.toResponse())
            else -> result.toErrorResponse()
        }
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteTask(
        @PathVariable id: Long,
    ): ResponseEntity<Any> {
        return when (val result = taskService.deleteTask(id)) {
            is ServiceResult.Deleted -> ResponseEntity.noContent().build()
            else -> result.toErrorResponse()
        }
    }
}

private fun ServiceResult<*>.toErrorResponse(): ResponseEntity<Any> = when (this) {
    is ServiceResult.Success -> error("Success is not an error state")
    is ServiceResult.Deleted -> error("Deleted is not an error state")
    is ServiceResult.NotFound -> problemDetailResponse(HttpStatus.NOT_FOUND, "Task not found.")
    is ServiceResult.ValidationError -> problemDetailResponse(HttpStatus.BAD_REQUEST, message)
    is ServiceResult.Conflict -> problemDetailResponse(HttpStatus.CONFLICT, message)
    is ServiceResult.VersionConflict -> problemDetailResponse(HttpStatus.CONFLICT, message)
    is ServiceResult.UnexpectedError -> problemDetailResponse(HttpStatus.INTERNAL_SERVER_ERROR, message)
}

private fun problemDetailResponse(status: HttpStatus, detail: String): ResponseEntity<Any> =
    ResponseEntity.status(status)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(ProblemDetail.forStatusAndDetail(status, detail))
