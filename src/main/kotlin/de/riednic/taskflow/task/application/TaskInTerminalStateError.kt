package de.riednic.taskflow.task.application

import de.riednic.taskflow.common.application.ErrorCategory
import de.riednic.taskflow.common.application.ServiceResult

data class TaskInTerminalStateError(override val message: String) : ServiceResult.Error {
    override val code = "TASK_IN_TERMINAL_STATE"
    override val category = ErrorCategory.CONFLICT
}
