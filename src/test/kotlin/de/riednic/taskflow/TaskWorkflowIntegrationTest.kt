package de.riednic.taskflow

import com.jayway.jsonpath.JsonPath
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration::class)
class TaskWorkflowIntegrationTest(
    @Autowired private val mockMvc: MockMvc,
) {

    @Test
    fun `login, create a task and transition it to IN_PROGRESS`() {
        val loginResponse = mockMvc.post("/api/v1/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"email":"admin@taskflow.test","password":"test-admin-password"}"""
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        val token = JsonPath.read<String>(loginResponse, "$.token")

        val createTaskResponse = mockMvc.post("/api/v1/tasks") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """{"title":"Integration test task","status":"TODO","priority":"MEDIUM"}"""
        }.andExpect {
            status { isCreated() }
        }.andReturn().response.contentAsString

        val taskId = (JsonPath.read<Any>(createTaskResponse, "$.id") as Number).toLong()
        val version = (JsonPath.read<Any>(createTaskResponse, "$.version") as Number).toLong()

        val transitionResponse = mockMvc.post("/api/v1/tasks/$taskId/status") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
            content = """{"status":"IN_PROGRESS","version":$version}"""
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        assertEquals("IN_PROGRESS", JsonPath.read(transitionResponse, "$.status"))
    }
}
