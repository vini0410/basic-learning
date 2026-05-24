package com.notification.notificationapp.integration.config

import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfig {

    @Bean
    fun messageConverter(): MessageConverter = JacksonJsonMessageConverter()
}