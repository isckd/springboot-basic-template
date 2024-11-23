package com.example.demo.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.ClientRequest

@Configuration
class WebClientConfig {

    @Value("\${spring.application.name}")
    private lateinit var applicationName: String

    @Bean
    fun webClient(): WebClient {
        return WebClient.builder()
            .filter { request, next ->
                val newRequest = ClientRequest.from(request)
                    .header("X-Component-Name", applicationName)
                    .build()
                next.exchange(newRequest)
            }
            .build()
    }
}
