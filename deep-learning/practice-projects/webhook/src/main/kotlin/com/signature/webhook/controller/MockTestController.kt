package com.signature.webhook.controller

import com.signature.webhook.feign.FeignSender
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MockTestController(
    private val feignSender: FeignSender
) {

    @GetMapping("/test-mock")
    fun testMock(): ResponseEntity<String> {
        feignSender.sendMessage("Hello, World!")
        return ResponseEntity.ok("This is a mock response")
    }
}