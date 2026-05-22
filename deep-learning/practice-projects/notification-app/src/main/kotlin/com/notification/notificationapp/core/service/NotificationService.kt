package com.notification.notificationapp.core.service

import com.notification.notificationapp.core.processor.NotificationProcessor
import org.springframework.stereotype.Service

@Service
class NotificationService(processors: List<NotificationProcessor>) {
    private val processorMap = processors.associateBy { it.type }

    fun process(type: String, metadata: Map<String, Any>, message: String) {
        val processor = processorMap[type] ?: throw IllegalArgumentException("Unknown type: $type")
        processor.process(metadata, message)
    }
}