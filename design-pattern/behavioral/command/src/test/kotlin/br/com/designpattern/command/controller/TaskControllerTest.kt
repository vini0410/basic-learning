package br.com.designpattern.command.controller

import br.com.designpattern.command.domain.CommandResult
import br.com.designpattern.command.domain.Task
import br.com.designpattern.command.domain.TaskRepository
import br.com.designpattern.command.domain.command.Command
import br.com.designpattern.command.service.CommandProcessor
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TaskControllerTest {
    private val commandProcessor: CommandProcessor = mockk()
    private val taskRepository: TaskRepository = mockk()
    private val controller = TaskController(commandProcessor, taskRepository)
    
    @BeforeEach
    fun setup() {
        every { commandProcessor.execute(any<Command>()) } returns CommandResult(true, "Success", 1)
    }
    
    @Test
    fun `should create task successfully`() {
        val task = Task(name = "Test Task", description = "Test Description", scheduledTime = "2024-08-09T10:00:00")
        
        val result = controller.createTask(task)
        
        assertTrue(result.success)
    }
    
    @Test
    fun `should update task status successfully`() {
        val result = controller.updateTaskStatus(1, "IN_PROGRESS")
        
        assertTrue(result.success)
    }
    
    @Test
    fun `should delete task successfully`() {
        val result = controller.deleteTask(1)
        
        assertTrue(result.success)
    }
    
    @Test
    fun `should undo last command successfully`() {
        every { commandProcessor.undoLast() } returns CommandResult(true, "Success", 1)
        
        val result = controller.undoLastCommand()
        
        assertTrue(result.success)
    }

//    @Test
//    fun `should create task successfully`() {
//        val task = Task(name = "Test Task", description = "Test Description", scheduledTime = "2024-08-09T10:00:00")
//
//        mockMvc.perform(
//            post("/api/tasks")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(task))
//        )
//            .andExpect(status().isOk())
//    }

//    @Test
//    fun `should update task status successfully`() {
//        mockMvc.perform(
//            put("/api/tasks/1/status")
//                .param("status", "IN_PROGRESS")
//        )
//            .andExpect(status().isOk())
//    }
//
//    @Test
//    fun `should delete task successfully`() {
//        mockMvc.perform(delete("/api/tasks/1"))
//            .andExpect(status().isOk())
//    }
//
//    @Test
//    fun `should undo last command successfully`() {
//        mockMvc.perform(post("/api/tasks/undo"))
//            .andExpect(status().isOk())
//    }
}
