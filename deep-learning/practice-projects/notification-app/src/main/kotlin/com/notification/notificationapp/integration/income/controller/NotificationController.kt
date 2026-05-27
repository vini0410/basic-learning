package com.notification.notificationapp.integration.income.controller

import com.notification.notificationapp.core.service.NotificationService
import com.notification.notificationapp.integration.income.NotificationMessage
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/notify")
class NotificationController(
    private val service: NotificationService,
) {

    @PostMapping
    fun notify(@RequestBody payload: NotificationMessage): ResponseEntity<String> {
        service.process(payload.type, payload.metadata, payload.message)
        return ResponseEntity.ok("Sended!!!")
    }
}