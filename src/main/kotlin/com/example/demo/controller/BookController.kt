package com.example.demo.controller

import com.example.demo.common.error.BusinessException
import com.example.demo.common.error.ErrorCode
import com.example.demo.entity.Book
import com.example.demo.repository.BookRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Book", description = "Book management APIs")
@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookRepository: BookRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Get all books")
    @GetMapping
    fun getAllBooks(): List<Book> {
        log.info("Fetching all books")
        return bookRepository.findAll().also {
            if (it.isEmpty()) { log.warn("No books found") }
        }
    }

    @Operation(
        summary = "Get a book by ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Book found"),
            ApiResponse(responseCode = "404", description = "Book not found")
        ]
    )
    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Long): ResponseEntity<Book> {
        log.info("Fetching book with id: {}", id)
        return bookRepository.findById(id)
            .map { 
                log.debug("Found book: {}", it)
                ResponseEntity.ok(it)
            }
            .orElseThrow {
                log.warn("Book not found with id: {}", id)
                throw BusinessException(ErrorCode.BOOK_NOT_FOUND)
            }
    }

    @Operation(summary = "Create a new book")
    @PostMapping
    fun createBook(@RequestBody book: Book): Book {
        log.info("Creating new book: {}", book)
        return bookRepository.save(book).also { log.debug("Created book with id: {}", it.id) }
    }

    @Operation(
        summary = "Delete a book by ID",
        responses = [
            ApiResponse(responseCode = "204", description = "Book deleted"),
            ApiResponse(responseCode = "404", description = "Book not found")
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: Long): ResponseEntity<Void> {
        log.info("Deleting book with id: {}", id)
        return if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id)
            log.debug("Deleted book with id: {}", id)
            ResponseEntity.noContent().build()
        } else {
            log.warn("Book not found with id: {}", id)
            throw BusinessException(ErrorCode.BOOK_NOT_FOUND)
        }
    }

    @Operation(
        summary = "Update a book by ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Book updated"),
            ApiResponse(responseCode = "404", description = "Book not found")
        ]
    )
    @PutMapping("/{id}")
    fun updateBook(@PathVariable id: Long, @RequestBody book: Book): ResponseEntity<Book> {
        log.info("Updating book with id: {}", id)
        return if (bookRepository.existsById(id)) {
            book.id = id
            ResponseEntity.ok(bookRepository.save(book).also { log.debug("Updated book: {}", it) })
        } else {
            log.warn("Book not found with id: {}", id)
            throw BusinessException(ErrorCode.BOOK_NOT_FOUND)
        }
    }
}
