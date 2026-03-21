package com.factory.study.models

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val errorCode: String,
    val message: String
)
