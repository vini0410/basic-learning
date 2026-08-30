package br.com.designpattern.command.service

import br.com.designpattern.command.domain.CommandResult
import br.com.designpattern.command.domain.command.Command
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class CommandProcessorTest {
    private lateinit var commandProcessor: CommandProcessor
    private lateinit var mockCommand: Command

    @BeforeEach
    fun setup() {
        commandProcessor = CommandProcessor()
        mockCommand = mockk()

        every { mockCommand.execute() } returns CommandResult(true, "Success", 1)
        every { mockCommand.undo() } returns CommandResult(true, "Undone", 1)
    }

    @Test
    fun `should execute command successfully`() {
        val result = commandProcessor.execute(mockCommand)
        
        assertTrue(result.success)
        assertEquals("Success", result.message)
        assertEquals(1, commandProcessor.getCommandHistorySize())
    }

    @Test
    fun `should undo last command successfully`() {
        // Execute a command first
        commandProcessor.execute(mockCommand)
        
        // Then undo it
        val result = commandProcessor.undoLast()
        
        assertTrue(result.success)
        assertEquals("Undone", result.message)
        assertEquals(0, commandProcessor.getCommandHistorySize())
    }

    @Test
    fun `should return failure when undoing with empty history`() {
        val result = commandProcessor.undoLast()
        
        assertFalse(result.success)
        assertEquals("No commands to undo", result.message)
    }
}
