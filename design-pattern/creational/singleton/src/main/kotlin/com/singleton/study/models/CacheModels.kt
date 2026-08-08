package com.singleton.study.models

import java.time.Instant

data class CacheEntry(
    val key: String,
    val value: Any,
    val expirationTime: Instant = Instant.now().plusSeconds(3600) // 1 hour default
)

data class CacheStats(
    val hits: Long,
    val misses: Long,
    val size: Int
)