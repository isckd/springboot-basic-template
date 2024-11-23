package com.example.demo.service

import com.example.demo.entity.Book
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono

@Service
class WebClientSender(
    private val webClient: WebClient
) {
    private var baseUrl = "http://localhost:8080"
    
    // 모든 책 조회
    fun getAllBooks(): List<Book> {
        return webClient
            .get()
            .uri("$baseUrl/api/books")
            .retrieve()
            .bodyToMono<List<Book>>()
            .block() ?: emptyList()
    }
    
    // 특정 ID의 책 조회
    fun getBookById(id: Long): Book? {
        return webClient
            .get()
            .uri("$baseUrl/api/books/{id}", id)
            .retrieve()
            .bodyToMono<Book>()
            .block()
    }
    
    // 새로운 책 생성
    fun createBook(book: Book): Book {
        return webClient
            .post()
            .uri("$baseUrl/api/books")
            .bodyValue(book)
            .retrieve()
            .bodyToMono<Book>()
            .block()!!
    }
    
    // 책 정보 업데이트
    fun updateBook(id: Long, book: Book): Book? {
        return webClient
            .put()
            .uri("$baseUrl/api/books/{id}", id)
            .bodyValue(book)
            .retrieve()
            .bodyToMono<Book>()
            .block()
    }
    
    // 책 삭제
    fun deleteBook(id: Long): Boolean {
        return try {
            webClient
                .delete()
                .uri("$baseUrl/api/books/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block()
            true
        } catch (e: Exception) {
            false
        }
    }
}