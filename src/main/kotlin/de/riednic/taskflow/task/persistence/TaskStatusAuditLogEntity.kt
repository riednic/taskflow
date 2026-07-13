package de.riednic.taskflow.task.persistence

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
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "task_status_audit_log")
@EntityListeners(AuditingEntityListener::class)
class TaskStatusAuditLogEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "task_id", nullable = false)
    val taskId: Long,

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", nullable = false, columnDefinition = "task_status")
    val fromStatus: TaskStatus,

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, columnDefinition = "task_status")
    val toStatus: TaskStatus,

    @Column(name = "changed_by", nullable = false)
    val changedBy: Long,

    @CreatedDate
    @Column(name = "changed_at", nullable = false, updatable = false)
    var changedAt: Instant? = null,
)
