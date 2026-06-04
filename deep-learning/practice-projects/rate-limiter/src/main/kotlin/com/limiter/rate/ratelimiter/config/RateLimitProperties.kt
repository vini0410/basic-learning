package com.limiter.rate.ratelimiter.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@Component
@ConfigurationProperties(prefix = "app.rate-limit")
data class RateLimitProperties(
    var maxRequests: Int = 5,

    @DurationUnit(ChronoUnit.SECONDS)
    var window: Duration = Duration.ofSeconds(10)
)