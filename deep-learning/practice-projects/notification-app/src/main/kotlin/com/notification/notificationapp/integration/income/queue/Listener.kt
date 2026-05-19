package com.notification.notificationapp.integration.income.queue

import com.rabbitmq.client.Channel
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component

@Component
class Listener {

    private val logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = [$$"${spring.rabbitmq.name}"])
    fun listener(
        message: NotificationMessage,
        channel: Channel,
        @Header(AmqpHeaders.DELIVERY_TAG) deliveryTag: Long,
    ) {
        logger.info("Listener started")

        logger.info("received message: {}", message.content)
        try {
            channel.basicAck(deliveryTag, false)
        } catch (e: Exception) {
            logger.error(e.message)
            channel.basicNack(deliveryTag, false, false)
        }

    }
}