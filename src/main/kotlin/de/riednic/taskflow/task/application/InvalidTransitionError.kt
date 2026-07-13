package de.riednic.taskflow.task.application

import de.riednic.taskflow.common.application.ErrorCategory
import de.riednic.taskflow.common.application.ServiceResult

data class InvalidTransitionError(override val message: String) : ServiceResult.Error {
    override val code = "INVALID_TRANSITION"
    override val category = ErrorCategory.CONFLICT
}
