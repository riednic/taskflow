package de.riednic.taskflow.statistics.controller

import de.riednic.taskflow.common.application.ServiceResult
import de.riednic.taskflow.common.controller.toResponseEntity
import de.riednic.taskflow.statistics.application.StatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("api/v1/statistics")
class StatisticsController(
    private val statisticsService: StatisticsService,
) {

    @PreAuthorize("hasAnyAuthority('ADMIN', 'REVIEWER')")
    @GetMapping("/tasks")
    fun getTaskStatistics(): ResponseEntity<Any> {
        return when (val result = statisticsService.getTaskStatistics()) {
            is ServiceResult.Success -> ResponseEntity.ok(result.value.toResponse())
            is ServiceResult.Error -> result.toResponseEntity()
        }
    }
}