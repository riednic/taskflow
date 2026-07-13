package de.riednic.taskflow.task.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface SpringDataTaskStatusAuditLogRepository : JpaRepository<TaskStatusAuditLogEntity, Long>
