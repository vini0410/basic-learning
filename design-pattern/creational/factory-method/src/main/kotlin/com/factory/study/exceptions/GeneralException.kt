package com.factory.study.exceptions

enum class ErrorType {
    NOT_FOUND,
    INVALID_PAYLOAD,
    INTERNAL_ERROR
}

class GeneralException(
    val type: ErrorType,
    override val message: String,
    val errorCode: String? = null
) : RuntimeException(message)
