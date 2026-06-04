package com.limiter.rate.ratelimiter.filter

import com.limiter.rate.ratelimiter.RateLimiterService
import com.limiter.rate.ratelimiter.config.RateLimitProperties
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class RateLimitFilter(
    private val rateLimiterService: RateLimiterService,
    private val rateLimitProperties: RateLimitProperties
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val clientIp = request.remoteAddr ?: "unknown"

        val maxRequests = rateLimitProperties.maxRequests
        val windowSeconds = rateLimitProperties.window.toSeconds()

        // Se a configuração for inválida (menor que 1s), permitimos a requisição
        // e evitamos processar o rate limit para não causar erros matemáticos.
        if (windowSeconds < 1) {
            filterChain.doFilter(request, response)
            return
        }

        val allowed = rateLimiterService.isAllowed("ip:$clientIp", maxRequests, windowSeconds)

        if (allowed) {
            filterChain.doFilter(request, response)
        } else {
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.contentType = "application/json"
            response.writer.write("""{"error": "Too Many Requests", "message": "Limit of $maxRequests requests per ${windowSeconds}s exceeded."}""")
        }
    }
}