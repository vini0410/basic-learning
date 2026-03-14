package com.abstract.study.service

import com.abstract.study.exceptions.ErrorType
import com.abstract.study.interfaces.NotificationFactory
import com.abstract.study.models.*
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

class NotificationService(
    private val factories: Map<ChannelType, NotificationFactory>
) {
    private val logger = LoggerFactory.getLogger(NotificationService::class.java)

    fun sendNotification(request: NotificationRequest): NotificationResult {
        logger.info("Iniciando processo de notificação - Canal: {}, Destinatário: {}, Conteúdo: {}", 
            request.channelType, request.recipient, request.content)

        val type = try {
            ChannelType.valueOf(request.channelType.uppercase())
        } catch (e: Exception) {
            NotificationErrorHandler.report(
                channel = request.channelType,
                message = "Tipo de notificação '${request.channelType}' não suportado.",
                type = ErrorType.INVALID_PAYLOAD,
                errorCode = "INVALID_CHANNEL"
            )
        }

        val factory = factories[type] 
            ?: NotificationErrorHandler.report(
                channel = request.channelType,
                message = "O canal ${request.channelType} não está configurado.",
                type = ErrorType.NOT_FOUND,
                errorCode = "NOT_CONFIGURED"
            )

        val formatter = factory.createFormatter()
        val sender = factory.createSender()
        val logger = factory.createLogger()

        return try {
            val formattedMessage = formatter.format(request.content)
            sender.send(formattedMessage)
            
            NotificationResult(
                success = true, 
                message = "Notificação enviada com sucesso via ${request.channelType}",
                timestamp = LocalDateTime.now().toString()
            )
        } catch (e: Exception) {
            logger.log("Falha no envio: ${e.message}")

            NotificationErrorHandler.report(
                channel = request.channelType,
                message = "Falha ao enviar via ${request.channelType}: ${e.message}",
                type = ErrorType.COMMUNICATION_FAILURE,
                errorCode = "SEND_FAILED",
                cause = e
            )
        }
    }
}
