package de.riednic.taskflow.task.persistence

import de.riednic.taskflow.task.domain.TaskFilter
import org.springframework.data.jpa.domain.Specification

private fun <T> equal(
    field: String,
    value: T?
): Specification<TaskEntity>? =
    value?.let {
        Specification { root, _, cb ->
            cb.equal(root.get<T>(field), it)
        }
    }

fun TaskFilter.toSpecification(): Specification<TaskEntity> =
    listOfNotNull(
        equal("status", status),
        equal("priority", priority),
        equal("assignedTo", assignedTo)
    ).reduceOrNull(Specification<TaskEntity>::and)
        ?: Specification { _, _, cb -> cb.conjunction() }
