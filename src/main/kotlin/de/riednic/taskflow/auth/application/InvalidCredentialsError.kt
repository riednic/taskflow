package de.riednic.taskflow.auth.application

import de.riednic.taskflow.common.application.ErrorCategory
import de.riednic.taskflow.common.application.ServiceResult

data class InvalidCredentialsError(override val message: String) : ServiceResult.Error {
    override val code = "INVALID_CREDENTIALS"
    override val category = ErrorCategory.UNAUTHORIZED
}
