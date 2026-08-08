package com.singleton.study.service

import com.singleton.study.models.CacheEntry
import com.singleton.study.models.CacheStats
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Service
class CacheManager {
    private val cache: MutableMap<String, CacheEntry> = ConcurrentHashMap()
    private var hits: Long = 0
    private var misses: Long = 0
    
    companion object {
        @Volatile
        private var instance: CacheManager? = null
        
        fun getInstance(): CacheManager {
            return instance ?: synchronized(this) {
                instance ?: CacheManager().also { instance = it }
            }
        }
    }
    
    fun put(key: String, value: Any, ttlSeconds: Long = 3600): Boolean {
        val entry = CacheEntry(
            key = key,
            value = value,
            expirationTime = Instant.now().plusSeconds(ttlSeconds)
        )
        cache[key] = entry
        return true
    }
    
    fun get(key: String): Any? {
        val entry = cache[key] ?: return null.apply { misses++ }
        
        if (entry.expirationTime.isBefore(Instant.now())) {
            cache.remove(key)
            misses++
            return null
        }
        
        hits++
        return entry.value
    }
    
    fun getStats(): CacheStats {
        return CacheStats(
            hits = hits,
            misses = misses,
            size = cache.size
        )
    }
    
    fun clear() {
        cache.clear()
    }
}