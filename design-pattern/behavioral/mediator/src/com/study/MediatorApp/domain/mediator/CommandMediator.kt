package com.study.MediatorApp.domain.mediator

import com.study.MediatorApp.domain.command.Command

/**
 * Interface do Mediador de Comandos (Command Bus).
 * Desacopla o emissor da requisicao do manipulador final.
 */
interface CommandMediator {
    fun <R> send(command: Command<R>): R
}
