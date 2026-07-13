package de.riednic.taskflow.task.persistence

import de.riednic.taskflow.task.domain.NewTask
import de.riednic.taskflow.task.domain.Task
import de.riednic.taskflow.task.domain.TaskPriority
import de.riednic.taskflow.task.domain.TaskStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import kotlin.jvm.Throws
import kotlin.time.toKotlinInstant

@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener::class)
class TaskEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 255)
    var title: String,

    @Column(length = 1000)
    var description: String? = null,

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "task_status")
    var status: TaskStatus,

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "task_priority")
    var priority: TaskPriority,

    @Column(name = "assigned_to")
    var assignedTo: Long? = null,

    @Version
    @Column(nullable = false)
    var version: Long = 0,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null,

    @LastModifiedDate
    @Column(nullable = false)
    var updatedAt: Instant? = null
)

fun NewTask.toEntity(): TaskEntity {
    return TaskEntity(
        title = title,
        description = description,
        status = status,
        priority = priority,
        assignedTo = assignedTo,
    )
}

@Throws(IllegalArgumentException::class)
fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        status = status,
        priority = priority,
        assignedTo = assignedTo,
        version = version,
        createdAt = requireNotNull(createdAt) {
            "createdAt was not populated by JPA auditing for TaskEntity id=$id"
        }.toKotlinInstant(),
        updatedAt = requireNotNull(updatedAt) {
            "updatedAt was not populated by JPA auditing for TaskEntity id=$id"
        }.toKotlinInstant()
    )
}
