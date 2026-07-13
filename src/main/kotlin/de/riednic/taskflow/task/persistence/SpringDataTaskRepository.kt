package de.riednic.taskflow.task.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface SpringDataTaskRepository : JpaRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity>
