package com.example.demo.config

import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

/**
 * MDC 컬렉션에서 Custom Header 값을 불러와 HTTP Request / Response 로그를 출력
 *
 * 아래 예시)
 * 2024-11-23 23:52:00.418 INFO  [http-nio-8080-exec-1] c.e.d.c.CustomRequestLoggingFilter T[20241123235200-8138947] - [SBT-REQ] POST /api/books {"id":null,"name":"Test Book","category":"Test Category"}
 * 	Headers={accept-encoding=gzip, user-agent=ReactorNetty/1.1.22, host=localhost:8080, accept=*, x-component-name=SBT, content-type=application/json, content-length=57}
 * 2024-11-23 23:52:00.545 INFO  [http-nio-8080-exec-1] c.e.d.c.CustomRequestLoggingFilter T[20241123235200-8138947] - [SBT-RES] 200 126ms /api/books {"id":1,"name":"Test Book","category":"Test Category"}
 * 	Headers={X-Trace-Id=20241123235200-8138947}
 *
 *
 */
@Component
class CustomRequestLoggingFilter : OncePerRequestFilter() {

    companion object {
        private val log = LoggerFactory.getLogger(CustomRequestLoggingFilter::class.java)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestWrapper = ContentCachingRequestWrapper(request)
        val responseWrapper = ContentCachingResponseWrapper(response)
        val startTime = System.currentTimeMillis()

        // Cache the request body
        var cachedBody: ByteArray? = null
        if (request.contentLength > 0) {
            cachedBody = requestWrapper.inputStream.readAllBytes()
            // Log request with the cached body
            logRequest(requestWrapper.method, requestWrapper.requestURI, cachedBody, getHeaders(requestWrapper))
        } else {
            logRequest(requestWrapper.method, requestWrapper.requestURI, null, getHeaders(requestWrapper))
        }

        // Create a new request wrapper with the cached body
        val finalRequest = if (cachedBody != null) {
            object : ContentCachingRequestWrapper(request) {
                private val cachedInputStream = object : ServletInputStream() {
                    private val delegate = ByteArrayInputStream(cachedBody)

                    override fun read(): Int = delegate.read()
                    override fun available(): Int = delegate.available()
                    override fun isFinished(): Boolean = delegate.available() == 0
                    override fun isReady(): Boolean = true
                    override fun setReadListener(readListener: ReadListener?) {
                        throw UnsupportedOperationException("ReadListener is not supported")
                    }
                }
                override fun getInputStream(): ServletInputStream = cachedInputStream
            }
        } else {
            requestWrapper
        }
        
        filterChain.doFilter(finalRequest, responseWrapper)

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        logResponse(requestWrapper, responseWrapper, duration)

        responseWrapper.copyBodyToResponse()
    }

    private fun logRequest(method: String, uri: String, body: ByteArray?, headers: Map<String, String>) {
        val content = body?.let { 
            String(it, StandardCharsets.UTF_8)
                .replace("\n", "")
                .replace("\r", "")
                .replace(" +".toRegex(), " ") 
        } ?: ""
        val componentName = MDC.get("componentName") ?: "UNKNOWN"
        log.info("[$componentName-REQ] $method $uri $content \n\tHeaders=$headers")
    }

    private fun logResponse(request: ContentCachingRequestWrapper, response: ContentCachingResponseWrapper, duration: Long) {
        val content = String(response.contentAsByteArray, StandardCharsets.UTF_8)
        val componentName = MDC.get("componentName") ?: "unknown"
        log.info("[$componentName-RES] ${response.status} ${duration}ms ${request.requestURI} $content \n\tHeaders=${getResponseHeaders(response)}")
    }

    private fun getHeaders(request: HttpServletRequest): Map<String, String> {
        return request.headerNames.toList()
            .associateWith { request.getHeader(it) }
    }

    private fun getResponseHeaders(response: HttpServletResponse): Map<String, String> {
        return response.headerNames
            .associateWith { response.getHeader(it) }
    }
}
