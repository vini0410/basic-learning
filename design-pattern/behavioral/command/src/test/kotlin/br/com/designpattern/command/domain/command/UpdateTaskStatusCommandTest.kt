package br.com.designpattern.command.domain.command

import br.com.designpattern.command.domain.Task
import br.com.designpattern.command.domain.CommandResult
import br.com.designpattern.command.domain.TaskRepository
import br.com.designpattern.command.domain.TaskStatus
import io.mockk.*
import java.util.Optional
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

class UpdateTaskStatusCommandTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var command: UpdateTaskStatusCommand

    @BeforeEach
    fun setup() {
        taskRepository = mockk()
        command = UpdateTaskStatusCommand(1, "COMPLETED", taskRepository)

        every { taskRepository.save(any()) } returns Task(id = 1, name = "Updated Task", description = "Updated Description", scheduledTime = "2024-08-09T10:00:00")
        every { taskRepository.findById(any()) } returns Optional.of(Task(id = 1, name = "Test Task", description = "Test Description", scheduledTime = "2024-08-09T10:00:00", status = TaskStatus.PENDING))
    }

    @Test
    fun `should execute update task status command successfully`() {
        val result = command.execute()
        
        assertTrue(result.success)
        assertEquals("Task status updated to COMPLETED", result.message)
        assertNotNull(result.taskId)
    }

    @Test
    fun `should undo update task status command successfully`() {
        // First execute the command
        command.execute()
        
        // Then undo it
        val result = command.undo()
        
        assertTrue(result.success)
        assertEquals("Task status reverted to PENDING", result.message)
    }
}
