package com.notification.notificationapp.core.service

import com.notification.notificationapp.core.processor.NotificationProcessor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class NotificationService(processors: List<NotificationProcessor>) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val processorMap = processors.associateBy { it.type }

    fun process(type: String, metadata: Map<String, Any>, message: String) {
        logger.info("service: type: {}", type)
        val processor = processorMap[type] ?: throw IllegalArgumentException("Unknown type: $type")
        logger.info("service: processor: {}", processor.type)
        processor.process(metadata, message)
    }
}