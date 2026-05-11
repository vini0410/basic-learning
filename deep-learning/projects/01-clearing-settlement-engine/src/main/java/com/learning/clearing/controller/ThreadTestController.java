package com.learning.clearing.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class ThreadTestController {

    @GetMapping("/threads")
    public Map<String, Object> getThreadInfo() {
        Thread currentThread = Thread.currentThread();
        return Map.of(
                "threadName", currentThread.toString(),
                "isVirtual", currentThread.isVirtual(),
                "message", "Processando em uma Virtual Thread!"
        );
    }
}
