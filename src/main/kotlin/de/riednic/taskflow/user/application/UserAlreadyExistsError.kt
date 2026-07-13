package de.riednic.taskflow.user.application

import de.riednic.taskflow.common.application.ErrorCategory
import de.riednic.taskflow.common.application.ServiceResult

data class UserAlreadyExistsError(override val message: String) : ServiceResult.Error {
    override val code = "USER_ALREADY_EXISTS"
    override val category = ErrorCategory.CONFLICT
}