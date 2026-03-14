package com.abstract.study.exceptions

import io.ktor.http.*

enum class ErrorType {
    NOT_FOUND,
    INVALID_PAYLOAD,
    COMMUNICATION_FAILURE,
    INTERNAL_ERROR
}

open class GeneralException(
    val type: ErrorType,
    override val message: String,
    open val errorCode: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)

class NotificationException(
    override val errorCode: String,
    override val message: String,
    type: ErrorType = ErrorType.INTERNAL_ERROR,
    cause: Throwable? = null
) : GeneralException(type, message, errorCode, cause)
