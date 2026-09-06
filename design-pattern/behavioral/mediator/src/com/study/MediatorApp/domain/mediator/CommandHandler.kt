package com.study.MediatorApp.domain.mediator

import com.study.MediatorApp.domain.command.Command

/**
 * Interface dos manipuladores (handlers) de comandos.
 * Cada handler cuida exclusivamente de um tipo especifico de comando [C].
 */
interface CommandHandler<C : Command<R>, R> {
    fun handle(command: C): R
}
