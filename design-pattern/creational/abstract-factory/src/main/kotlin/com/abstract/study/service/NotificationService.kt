package com.abstract.study.service

import com.abstract.study.email.EmailFactory
import com.abstract.study.interfaces.NotificationFactory
import com.abstract.study.models.ChannelType
import com.abstract.study.models.NotificationRequest
import com.abstract.study.models.NotificationResult
import com.abstract.study.push.PushFactory
import com.abstract.study.sms.SmsFactory

import java.time.LocalDateTime

class NotificationService(
    private val factories: Map<ChannelType, NotificationFactory>
) {

    fun sendNotification(request: NotificationRequest): NotificationResult {
        // Seleção da Fábrica de forma desacoplada
        val factory = factories[request.channelType] 
            ?: throw IllegalArgumentException("Fábrica para o canal ${request.channelType} não registrada.")

        // Utilização da Fábrica para criar os produtos da família
        val formatter = factory.createFormatter()
        val sender = factory.createSender()
        val logger = factory.createLogger()

        return try {
            val formattedMessage = formatter.format(request.content)
            sender.send(formattedMessage)
            NotificationResult(
                success = true, 
                message = "Notificação enviada com sucesso via ${request.channelType}",
                timestamp = java.time.LocalDateTime.now().toString()
            )
        } catch (e: Exception) {
            logger.log(e.message ?: "Erro desconhecido")
            NotificationResult(
                success = false, 
                message = "Falha ao enviar via ${request.channelType}: ${e.message}",
                timestamp = java.time.LocalDateTime.now().toString()
            )
        }
    }
}
