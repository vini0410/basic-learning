package com.factory.study.plugins

import com.factory.study.exceptions.ErrorType
import com.factory.study.exceptions.GeneralException
import com.factory.study.models.ErrorResponse
import com.factory.study.models.PaymentMethod
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<GeneralException> { call, cause ->
            val status = when (cause.type) {
                ErrorType.NOT_FOUND -> HttpStatusCode.NotFound
                ErrorType.INVALID_PAYLOAD -> HttpStatusCode.BadRequest
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

        exception<BadRequestException> { call, cause ->
            val innerMessage = cause.cause?.message ?: cause.message ?: "Unknown error"

            val customMessage = when {
                innerMessage.contains("PaymentMethod") && innerMessage.contains("does not contain element") -> {
                    val invalidValue = innerMessage.substringAfter("name '").substringBefore("'")
                    PaymentMethod.invalidMessage(invalidValue)
                }

                else -> innerMessage
            }

            call.respond(
                HttpStatusCode.BadRequest,
                ErrorResponse(
                    errorCode = "BAD_REQUEST",
                    message = "Requisição inválida: $customMessage"
                )
            )
        }

        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(
                    errorCode = "INTERNAL_SERVER_ERROR",
                    message = "Ocorreu um erro inesperado: ${cause.message ?: "Erro desconhecido"}"
                )
            )
        }
    }
}
