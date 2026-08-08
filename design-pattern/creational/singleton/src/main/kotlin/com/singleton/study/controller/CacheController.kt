package com.singleton.study.controller

import com.singleton.study.models.CacheStats
import com.singleton.study.service.CacheManager
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cache")
class CacheController {
    private val cacheManager = CacheManager.getInstance()
    
    @PostMapping("/put")
    fun putValue(@RequestParam key: String, @RequestParam value: String): ResponseEntity<String> {
        cacheManager.put(key, value)
        return ResponseEntity.ok("Value stored in cache with key: $key")
    }
    
    @GetMapping("/get")
    fun getValue(@RequestParam key: String): ResponseEntity<Any> {
        val value = cacheManager.get(key)
        return if (value != null) {
            ResponseEntity.ok(value)
        } else {
            ResponseEntity.notFound().build()
        }
    }
    
    @GetMapping("/stats")
    fun getStats(): ResponseEntity<CacheStats> {
        val stats = cacheManager.getStats()
        return ResponseEntity.ok(stats)
    }
    
    @DeleteMapping("/clear")
    fun clearCache(): ResponseEntity<String> {
        cacheManager.clear()
        return ResponseEntity.ok("Cache cleared successfully")
    }
}