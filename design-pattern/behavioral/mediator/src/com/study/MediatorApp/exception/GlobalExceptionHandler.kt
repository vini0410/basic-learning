package com.study.MediatorApp.exception

import com.study.MediatorApp.dto.ErrorResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(OrderNotFoundException::class)
    fun handleOrderNotFound(ex: OrderNotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn { "Recurso nao encontrado: ${ex.message}" }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                error = "Not Found",
                message = ex.message ?: "Recurso nao encontrado"
            )
        )
    }

    @ExceptionHandler(InsufficientStockException::class, InvalidOrderStateException::class, IllegalArgumentException::class)
    fun handleBadRequest(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.warn { "Requisicao invalida: ${ex.message}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                error = "Bad Request",
                message = ex.message ?: "Parametros ou regras de negocio invalidas"
            )
        )
    }

    @ExceptionHandler(HandlerNotFoundException::class)
    fun handleHandlerNotFound(ex: HandlerNotFoundException): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "Erro no bus de mediador: ${ex.message}" }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = "Mediator Handler Missing",
                message = ex.message ?: "Manipulador do comando nao encontrado"
            )
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error(ex) { "Erro inesperado: ${ex.message}" }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                error = "Internal Server Error",
                message = ex.message ?: "Erro interno do servidor"
            )
        )
    }
}
