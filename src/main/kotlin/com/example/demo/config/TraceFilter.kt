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

/**
 * HTTP Servlet request, response 의 Custom Header 값을 읽어서 MDC 컬렉션에 저장하는 Filter
 * MDC 값은 CustomRequestLoggingFilter, logback-spring.xml 등에서 사용된다.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class TraceFilter : OncePerRequestFilter() {

    companion object {
        private const val TRACE_ID_HEADER = "X-Trace-Id"
        private const val TRACE_ID_KEY = "traceId"
        private const val COMPONENT_NAME_HEADER = "X-Component-Name"
        private const val COMPONENT_NAME_KEY = "componentName"
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val traceId = request.getHeader(TRACE_ID_HEADER) ?: generateTraceId()
            val componentName = request.getHeader(COMPONENT_NAME_HEADER) ?: "UNKNOWN"
            
            MDC.put(TRACE_ID_KEY, traceId)
            MDC.put(COMPONENT_NAME_KEY, componentName)
            
            response.addHeader(TRACE_ID_HEADER, traceId)
            
            filterChain.doFilter(request, response)
        } finally {
            MDC.remove(TRACE_ID_KEY)
            MDC.remove(COMPONENT_NAME_KEY)
        }
    }

    private fun generateTraceId(): String {
        val timestamp = LocalDateTime.now().format(dateFormatter)
        val uuid = UUID.randomUUID().toString().substring(0, 7)
        return "$timestamp-$uuid"
    }
}
