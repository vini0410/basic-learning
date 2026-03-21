package com.builder.study.plugins

import com.builder.study.exceptions.*
import com.builder.study.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("ExceptionHandling")

fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<GeneralException> { call, cause ->
            logger.error("Erro na aplicação [{}]: {} - Status: {}", 
                cause.errorCode ?: cause.type.name, 
                cause.message, 
                cause.httpStatus.value
            )
            
            call.respond(
                cause.httpStatus, 
                ErrorResponse(
                    errorCode = cause.errorCode ?: cause.type.name, 
                    message = cause.message
                )
            )
        }

        exception<io.ktor.server.plugins.BadRequestException> { call, cause ->
            logger.warn("Requisição inválida: {}", cause.message)
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(errorCode = "BAD_REQUEST", message = "O formato da requisição é inválido.")
            )
        }

        exception<Throwable> { call, cause ->
            logger.error("Erro inesperado detectado", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(errorCode = "INTERNAL_SERVER_ERROR", message = "Um erro interno ocorreu no servidor.")
            )
        }
    }
}
