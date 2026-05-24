package com.notification.notificationapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class NotificationAppApplication

fun main(args: Array<String>) {
	runApplication<NotificationAppApplication>(*args)
}