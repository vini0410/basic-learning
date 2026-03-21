package com.builder.study.plugins

import com.builder.study.builders.report
import com.builder.study.exceptions.ErrorType
import com.builder.study.exceptions.ReportBuilderException
import com.builder.study.models.OutputFormat
import com.builder.study.models.ReportRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Routing")

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Builder Pattern Study - Dynamic Report API (POST only)")
        }

        /**
         * Endpoint principal que utiliza o padrão Builder (via DSL)
         * para transformar um DTO de requisição em uma configuração complexa.
         */
        post("/report/generate") {
            logger.info("Recebendo requisição para gerar novo relatório.")
            val request = call.receive<ReportRequest>()

            val configuration = report {1
                logger.debug("Iniciando construção do relatório: {}", request.reportName)
                setTitle(request.reportName)
                
                // Converte string para Enum de forma segura ou lança erro para o handler
                val format = try {
                    OutputFormat.valueOf(request.format.uppercase())
                } catch (e: IllegalArgumentException) {
                    throw ReportBuilderException(
                        message = "Formato de saída inválido: ${request.format}. Formatos suportados: ${OutputFormat.entries.joinToString()}",
                        type = ErrorType.VALIDATION_ERROR,
                        errorCode = "INVALID_OUTPUT_FORMAT",
                        httpStatus = HttpStatusCode.BadRequest
                    )
                }
                setOutputFormat(format)

                // O Builder brilha ao lidar com a presença opcional de campos
                request.filterBy?.let { field ->
                    logger.debug("Aplicando filtro no campo: {}", field)
                    addFilter(field, "==", request.filterValue ?: "")
                }

                request.sortBy?.let { field ->
                    logger.debug("Ordenando pelo campo: {}", field)
                    addSorting(field)
                }

                request.limit?.let { 
                    logger.debug("Limitando relatório a {} itens", it)
                    setItemsPerPage(it)
                }
            }

            logger.info("Relatório '{}' configurado com sucesso.", configuration.title)
            call.respond(configuration)
        }
    }
}
