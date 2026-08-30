package br.com.designpattern.command.domain.command

import br.com.designpattern.command.domain.Task
import br.com.designpattern.command.domain.CommandResult

interface Command {
    fun execute(): CommandResult
    fun undo(): CommandResult
}
