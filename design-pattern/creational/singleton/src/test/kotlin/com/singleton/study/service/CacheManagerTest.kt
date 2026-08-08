package com.singleton.study.service

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CacheManagerTest {
    @Test
    
    fun `should store and retrieve value`() {
        val cacheManager = CacheManager.getInstance()
        val key = "test-key"
        val value = "test-value"
        
        cacheManager.put(key, value)
        val retrievedValue = cacheManager.get(key)
        
        assertEquals(value, retrievedValue)
    }
    
    fun `should return null for non-existent key`() {
        val cacheManager = CacheManager.getInstance()
        val result = cacheManager.get("non-existent-key")
        assertNull(result)
    }
    
    fun `should increment hits on successful retrieval`() {
        val cacheManager = CacheManager.getInstance()
        val key = "hit-test"
        val value = "some-value"
        
        cacheManager.put(key, value)
        cacheManager.get(key) // First call
        cacheManager.get(key) // Second call
        
        val stats = cacheManager.getStats()
        assertEquals(2, stats.hits)
    }
    
    fun `should increment misses on non-existent key`() {
        val cacheManager = CacheManager.getInstance()
        cacheManager.get("non-existent-key")
        cacheManager.get("another-non-existent-key")
        
        val stats = cacheManager.getStats()
        assertEquals(2, stats.misses)
    }
    
    fun `should clear cache`() {
        val cacheManager = CacheManager.getInstance()
        cacheManager.put("key1", "value1")
        cacheManager.put("key2", "value2")
        
        assertEquals(2, cacheManager.getStats().size)
        
        cacheManager.clear()
        assertEquals(0, cacheManager.getStats().size)
    }
}