package de.riednic.taskflow.common.controller

import org.hibernate.exception.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.HandlerMethodValidationException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<Any> =
        errorEntity(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            ex.bindingResult.fieldErrors.joinToString("; ") { "${it.field}: ${it.defaultMessage}" },
        )

    @ExceptionHandler(HandlerMethodValidationException::class)
    fun handleHandlerMethodValidation(ex: HandlerMethodValidationException): ResponseEntity<Any> =
        errorEntity(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", ex.message ?: "Invalid request.")

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<Any> =
        errorEntity(HttpStatus.FORBIDDEN, "FORBIDDEN", ex.message ?: "Access is denied.")

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<Any> =
        errorEntity(HttpStatus.CONFLICT, "CONFLICT", ex.message ?: "Data integrity violation.")

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<Any> {
        return errorEntity(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred.")
    }
}
