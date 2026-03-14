package com.abstract.study.plugins

import com.abstract.study.exceptions.ErrorType
import com.abstract.study.exceptions.GeneralException
import com.abstract.study.models.ErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<GeneralException> { call, cause ->
            val status = when (cause.type) {
                ErrorType.NOT_FOUND -> HttpStatusCode.NotFound
                ErrorType.INVALID_PAYLOAD -> HttpStatusCode.BadRequest
                ErrorType.COMMUNICATION_FAILURE -> HttpStatusCode.ServiceUnavailable
                ErrorType.INTERNAL_ERROR -> HttpStatusCode.InternalServerError
            }
            
            call.respond(
                status, 
                ErrorResponse(
                    errorCode = cause.errorCode ?: cause.type.name, 
                    message = cause.message
                )
            )
        }
        
        exception<io.ktor.server.plugins.BadRequestException> { call, cause ->
            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(errorCode = "BAD_REQUEST", message = "Requisição inválida: ${cause.cause?.message ?: cause.message}")
            )
        }

        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(errorCode = "INTERNAL_SERVER_ERROR", message = "Ocorreu um erro inesperado: ${cause.message ?: "Erro desconhecido"}")
            )
        }
    }
}
