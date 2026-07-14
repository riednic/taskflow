package de.riednic.taskflow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.data.redis.autoconfigure.DataRedisRepositoriesAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication(exclude = [DataRedisRepositoriesAutoConfiguration::class])
@EnableJpaAuditing
class TaskflowApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<TaskflowApplication>(*args)
}
