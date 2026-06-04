package com.limiter.rate.ratelimiter

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.slf4j.LoggerFactory
import java.time.Duration

@Service
class RateLimiterService(
    private val redisTemplate: StringRedisTemplate
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun isAllowed(key: String, maxRequests: Int, windowSizeSeconds: Long): Boolean {
        val windowKey = generateWindowKey(key, windowSizeSeconds)
        val currentCount = redisTemplate.opsForValue().increment(windowKey) ?: 1L

        if (currentCount == 1L) {
            redisTemplate.expire(windowKey, Duration.ofSeconds(windowSizeSeconds))
        }

        val allowed = currentCount <= maxRequests
        
        if (!allowed) {
            logger.warn("Rate limit exceeded for key: {} (Count: {}, Max: {})", key, currentCount, maxRequests)
        }

        return allowed
    }

    private fun generateWindowKey(key: String, windowSizeSeconds: Long): String {
        val currentTimeSeconds = System.currentTimeMillis() / 1000
        
        // Proteção contra divisão por zero
        val safeWindowSize = if (windowSizeSeconds < 1) 1L else windowSizeSeconds
        
        val windowId = currentTimeSeconds / safeWindowSize
        return "rate_limit:{$key}:$windowId"
    }
}