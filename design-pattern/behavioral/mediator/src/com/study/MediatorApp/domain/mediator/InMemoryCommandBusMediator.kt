package com.study.MediatorApp.domain.mediator

import com.study.MediatorApp.domain.command.Command
import com.study.MediatorApp.exception.HandlerNotFoundException
import mu.KotlinLogging
import org.springframework.core.GenericTypeResolver
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

private val logger = KotlinLogging.logger {}

@Component
class InMemoryCommandBusMediator(
    handlers: List<CommandHandler<*, *>>
) : CommandMediator {

    private val handlerRegistry = ConcurrentHashMap<Class<out Command<*>>, CommandHandler<*, *>>()

    init {
        handlers.forEach { handler ->
            val generics = GenericTypeResolver.resolveTypeArguments(handler.javaClass, CommandHandler::class.java)
            if (generics != null && generics.isNotEmpty()) {
                @Suppress("UNCHECKED_CAST")
                val commandClass = generics[0] as Class<out Command<*>>
                handlerRegistry[commandClass] = handler
                logger.info { "Registrado CommandHandler '${handler.javaClass.simpleName}' para o comando '${commandClass.simpleName}'" }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R> send(command: Command<R>): R {
        val commandClass = command.javaClass
        logger.info { "Mediator roteando comando: ${commandClass.simpleName}" }

        val handler = handlerRegistry[commandClass] as? CommandHandler<Command<R>, R>
            ?: throw HandlerNotFoundException(commandClass.simpleName)

        return handler.handle(command)
    }

    /**
     * Utilizado para testes unitarios para registrar manualmente handlers se necessario.
     */
    fun <C : Command<R>, R> registerHandler(commandClass: Class<C>, handler: CommandHandler<C, R>) {
        handlerRegistry[commandClass] = handler
    }
}
