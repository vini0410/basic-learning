package com.abstract.study.service

import com.abstract.study.exceptions.ErrorType
import com.abstract.study.exceptions.GeneralException
import org.slf4j.LoggerFactory

object NotificationErrorHandler {
    private val logger = LoggerFactory.getLogger(NotificationErrorHandler::class.java)
    
    fun report(
        channel: String, 
        message: String, 
        type: ErrorType, 
        errorCode: String? = null,
        cause: Throwable? = null
    ): Nothing {
        val logHeader = "Erro no envio da notificação: ${channel.uppercase()} - $message"
        
        if (cause != null) {
            logger.error(logHeader, cause)
        } else {
            logger.error(logHeader)
        }
        
        throw GeneralException(type, message, errorCode, cause)
    }
}
