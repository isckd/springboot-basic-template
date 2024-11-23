package com.example.demo.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class TraceFilter : OncePerRequestFilter() {

    companion object {
        private const val TRACE_ID_KEY = "traceId"
        private const val TRACE_ID_HEADER = "X-Trace-Id"
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val traceId = request.getHeader(TRACE_ID_HEADER) ?: generateTraceId()
            MDC.put(TRACE_ID_KEY, traceId)
            response.addHeader(TRACE_ID_HEADER, traceId)
            filterChain.doFilter(request, response)
        } finally {
            MDC.remove(TRACE_ID_KEY)
        }
    }

    private fun generateTraceId(): String {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        val uuid = UUID.randomUUID().toString().substring(0, 7)
        return "$timestamp-$uuid"
    }
}
