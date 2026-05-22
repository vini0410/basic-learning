package com.notification.notificationapp.integration.income.queue

import com.notification.notificationapp.core.service.NotificationService
import com.notification.notificationapp.integration.income.NotificationMessage
import com.rabbitmq.client.Channel
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class Listener(
    private val service: NotificationService,
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [$$"${app.queue.name}"])
    fun listener(
        message: NotificationMessage,
        channel: Channel,
        @Header(AmqpHeaders.DELIVERY_TAG) deliveryTag: Long,
    ) {
        logger.info("Listener started")

        logger.info("received message: {}", message.toString())
        try {
            service.process(
                message.type,
                message.metadata,
                message.message
            )
            channel.basicAck(deliveryTag, false)
        } catch (e: Exception) {
            logger.error(e.message)
            channel.basicNack(deliveryTag, false, false)
        }
    }
}