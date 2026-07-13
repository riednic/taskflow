package de.riednic.taskflow.common.controller

import de.riednic.taskflow.user.domain.UserAlreadyExistsException
import de.riednic.taskflow.user.domain.UserNotFoundException
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

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExists(ex: UserAlreadyExistsException): ResponseEntity<Any> =
        errorEntity(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", ex.message ?: "User already exists.")

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<Any> =
        errorEntity(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", ex.message ?: "User not found.")

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<Any> =
        errorEntity(HttpStatus.CONFLICT, "CONFLICT", ex.message ?: "Data integrity violation.")

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(ex: Exception): ResponseEntity<Any> {
        return errorEntity(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred.")
    }
}
