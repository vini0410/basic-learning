package br.com.designpattern.command.service

import br.com.designpattern.command.domain.Task
import br.com.designpattern.command.domain.CommandResult
import br.com.designpattern.command.domain.command.Command
import org.springframework.stereotype.Service

@Service
class CommandProcessor {
    private val commandHistory: MutableList<Command> = mutableListOf()

    fun execute(command: Command): CommandResult {
        val result = command.execute()
        if (result.success) {
            commandHistory.add(command)
        }
        return result
    }

    fun undoLast(): CommandResult {
        return if (commandHistory.isNotEmpty()) {
            val lastCommand = commandHistory.removeAt(commandHistory.size - 1)
            lastCommand.undo()
        } else {
            CommandResult(
                success = false,
                message = "No commands to undo"
            )
        }
    }

    fun getCommandHistorySize(): Int = commandHistory.size
}
