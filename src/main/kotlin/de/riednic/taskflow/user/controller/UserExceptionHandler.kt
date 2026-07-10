package de.riednic.taskflow.user.controller

import de.riednic.taskflow.user.domain.EmailAlreadyExistsException
import de.riednic.taskflow.user.domain.UserNotFoundException
import org.hibernate.exception.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(assignableTypes = [UserController::class])
class UserExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExists(ex: EmailAlreadyExistsException): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ex.message)

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ex.message)

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.message)
}
