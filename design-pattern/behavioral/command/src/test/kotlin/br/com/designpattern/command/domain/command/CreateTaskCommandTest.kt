package br.com.designpattern.command.domain.command

import br.com.designpattern.command.domain.Task
import br.com.designpattern.command.domain.CommandResult
import br.com.designpattern.command.domain.TaskRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

class CreateTaskCommandTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var command: CreateTaskCommand

    @BeforeEach
    fun setup() {
        taskRepository = mockk()
        val task = Task(id = 1, name = "Test Task", description = "Test Description", scheduledTime = "2024-08-09T10:00:00")
        command = CreateTaskCommand(task, taskRepository)

        every { taskRepository.save(any()) } returns Task(id = 1, name = "Test", description = "Test", scheduledTime = "2024-08-09T10:00:00")
        every { taskRepository.deleteById(any()) } just Runs
    }

    @Test
    fun `should execute create task command successfully`() {
        val result = command.execute()
        
        assertTrue(result.success)
        assertEquals("Task created successfully", result.message)
        assertNotNull(result.taskId)
    }

    @Test
    fun `should undo create task command successfully`() {
        // First execute the command
        command.execute()
        
        // Then undo it
        val result = command.undo()
        
        assertTrue(result.success)
        assertEquals("Task creation undone successfully", result.message)
    }
}
