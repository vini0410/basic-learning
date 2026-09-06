package com.study.MediatorApp.domain.mediator

import com.study.MediatorApp.domain.command.Command
import com.study.MediatorApp.exception.HandlerNotFoundException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private data class TestCommand(val input: String) : Command<String>
private data class UnregisteredCommand(val id: Int) : Command<Int>

class InMemoryCommandBusMediatorTest {

    private lateinit var mockHandler: CommandHandler<TestCommand, String>
    private lateinit var mediator: InMemoryCommandBusMediator

    @BeforeEach
    fun setUp() {
        mockHandler = mockk()
        mediator = InMemoryCommandBusMediator(emptyList())
        mediator.registerHandler(TestCommand::class.java, mockHandler)
    }

    @Test
    fun `deve rotear comando para o handler registrado com sucesso (happy path)`() {
        val command = TestCommand("Hello Mediator")
        every { mockHandler.handle(command) } returns "PROCESSED: Hello Mediator"

        val result = mediator.send(command)

        assertEquals("PROCESSED: Hello Mediator", result)
        verify(exactly = 1) { mockHandler.handle(command) }
    }

    @Test
    fun `deve lancar HandlerNotFoundException quando comando nao possui handler registrado (edge case)`() {
        val command = UnregisteredCommand(123)

        val exception = assertThrows(HandlerNotFoundException::class.java) {
            mediator.send(command)
        }

        assertEquals("Nenhum CommandHandler foi encontrado para o comando: UnregisteredCommand", exception.message)
    }

    @Test
    fun `deve propagar excecao lancada pelo handler durante a execucao`() {
        val command = TestCommand("Erro")
        every { mockHandler.handle(command) } throws IllegalStateException("Falha no processamento")

        val exception = assertThrows(IllegalStateException::class.java) {
            mediator.send(command)
        }

        assertEquals("Falha no processamento", exception.message)
    }
}
