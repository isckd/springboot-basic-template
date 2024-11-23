package com.example.demo.service

import com.example.demo.entity.Book
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClientResponseException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class WebClientSenderTest {
    @Autowired
    private lateinit var webClientSender: WebClientSender

    private lateinit var testBook: Book

    @BeforeEach
    fun setUp() {
        testBook = Book(
            name = "Test Book",
            category = "Test Category"
        )
    }

    @Test
    fun `책 생성 테스트`() {
        // when
        val createdBook = webClientSender.createBook(testBook)

        // then
        assert(createdBook.id != null)
        assert(createdBook.name == testBook.name)
        assert(createdBook.category == testBook.category)
    }

    @Test
    fun `책 조회 테스트`() {
        // given
        val createdBook = webClientSender.createBook(testBook)

        // when
        val foundBook = webClientSender.getBookById(createdBook.id!!)

        // then
        assert(foundBook != null)
        assert(foundBook?.id == createdBook.id)
        assert(foundBook?.name == createdBook.name)
        assert(foundBook?.category == createdBook.category)
    }

    @Test
    fun `모든 책 조회 테스트`() {
        // given
        val createdBook = webClientSender.createBook(testBook)

        // when
        val books = webClientSender.getAllBooks()

        // then
        assert(books.isNotEmpty())
        assert(books.any { it.id == createdBook.id })
    }

    @Test
    fun `책 업데이트 테스트`() {
        // given
        val createdBook = webClientSender.createBook(testBook)
        val updatedName = "Updated Book Name"
        createdBook.name = updatedName

        // when
        val updatedBook = webClientSender.updateBook(createdBook.id!!, createdBook)

        // then
        assert(updatedBook != null)
        assert(updatedBook?.id == createdBook.id)
        assert(updatedBook?.name == updatedName)
        assert(updatedBook?.category == createdBook.category)
    }

    @Test
    fun `책 삭제 테스트`() {
        // given
        val createdBook = webClientSender.createBook(testBook)

        // when
        val isDeleted = webClientSender.deleteBook(createdBook.id!!)

        // then
        assert(isDeleted)
        
        // verify deletion
        assertThrows<WebClientResponseException.NotFound> {
            webClientSender.getBookById(createdBook.id!!)
        }
    }

    @Test
    fun `존재하지 않는 책 조회 테스트`() {
        assertThrows<WebClientResponseException.NotFound> {
            webClientSender.getBookById(999)
        }
    }
}