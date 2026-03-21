package com.builder.study.exceptions

import io.ktor.http.*

enum class ErrorType {
    INVALID_CONFIGURATION,
    REPORT_GENERATION_ERROR,
    VALIDATION_ERROR,
    INTERNAL_ERROR
}

open class GeneralException(
    val type: ErrorType,
    override val message: String,
    val errorCode: String? = null,
    val httpStatus: HttpStatusCode = HttpStatusCode.InternalServerError,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class ReportBuilderException(
    message: String,
    type: ErrorType = ErrorType.INVALID_CONFIGURATION,
    errorCode: String = "REPORT_BUILDER_ERROR",
    httpStatus: HttpStatusCode = HttpStatusCode.BadRequest,
    cause: Throwable? = null
) : GeneralException(type, message, errorCode, httpStatus, cause)
