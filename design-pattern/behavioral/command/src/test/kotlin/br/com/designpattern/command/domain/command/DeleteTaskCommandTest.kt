package br.com.designpattern.command.domain.command

import br.com.designpattern.command.domain.Task
import br.com.designpattern.command.domain.CommandResult
import br.com.designpattern.command.domain.TaskRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue

class DeleteTaskCommandTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var command: DeleteTaskCommand

    @BeforeEach
    fun setup() {
        taskRepository = mockk()
        command = DeleteTaskCommand(1, taskRepository)

        every { taskRepository.deleteById(any()) } just Runs
    }

    @Test
    fun `should execute delete task command successfully`() {
        val result = command.execute()
        
        assertTrue(result.success)
        assertEquals("Task deleted successfully", result.message)
        assertEquals(1, result.taskId)
    }

    @Test
    fun `should fail to undo delete task command`() {
        // First execute the command
        command.execute()
        
        // Then try to undo it (should fail as we don't restore deleted tasks)
        val result = command.undo()
        
        assertFalse(result.success)
        assertEquals("Cannot undo delete operation - task not restored", result.message)
    }
}
