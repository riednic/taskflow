package de.riednic.taskflow.common.controller

import de.riednic.taskflow.common.application.ErrorCategory
import de.riednic.taskflow.common.application.ServiceResult
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.time.Instant

data class ErrorResponse(
    val error: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
)

fun ServiceResult.Error.toResponseEntity(): ResponseEntity<Any> =
    errorEntity(category.toHttpStatus(), code, message)

fun errorEntity(status: HttpStatus, error: String, message: String): ResponseEntity<Any> =
    ResponseEntity.status(status).body(ErrorResponse(error, message))

private fun ErrorCategory.toHttpStatus(): HttpStatus = when (this) {
    ErrorCategory.NOT_FOUND -> HttpStatus.NOT_FOUND
    ErrorCategory.BAD_REQUEST -> HttpStatus.BAD_REQUEST
    ErrorCategory.FORBIDDEN -> HttpStatus.FORBIDDEN
    ErrorCategory.CONFLICT -> HttpStatus.CONFLICT
    ErrorCategory.INTERNAL -> HttpStatus.INTERNAL_SERVER_ERROR
}
