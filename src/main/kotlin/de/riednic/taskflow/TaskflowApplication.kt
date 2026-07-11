package de.riednic.taskflow

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TaskflowApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<TaskflowApplication>(*args)
}
