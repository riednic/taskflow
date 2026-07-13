package de.riednic.taskflow.task.domain

@JvmInline
value class TaskId(val value: Long) {
    override fun toString() = value.toString()
}
