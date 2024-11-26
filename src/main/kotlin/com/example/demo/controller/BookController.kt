package com.example.demo.controller

import com.example.demo.common.error.BusinessException
import com.example.demo.common.error.ErrorCode
import com.example.demo.dto.BookRequestDto
import com.example.demo.dto.BookResponseDto
import com.example.demo.dto.ReviewRequestDto
import com.example.demo.dto.ReviewResponseDto
import com.example.demo.entity.Book
import com.example.demo.entity.Author
import com.example.demo.entity.Review
import com.example.demo.repository.BookRepository
import com.example.demo.repository.AuthorRepository
import com.example.demo.repository.ReviewRepository
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
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val reviewRepository: ReviewRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Operation(summary = "Get all books with authors")
    @GetMapping
    fun getAllBooks(): List<BookResponseDto> {
        log.info("Fetching all books")
        return bookRepository.findAll().map { BookResponseDto.from(it) }.also {
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
    fun getBookById(@PathVariable id: Long): ResponseEntity<BookResponseDto> {
        log.info("Fetching book with id: {}", id)
        return bookRepository.findById(id)
            .map { 
                log.debug("Found book: {}", it)
                ResponseEntity.ok(BookResponseDto.from(it))
            }
            .orElseThrow {
                log.warn("Book not found with id: {}", id)
                throw BusinessException(ErrorCode.BOOK_NOT_FOUND)
            }
    }

    @Operation(summary = "Create a new book with author")
    @PostMapping
    fun createBook(@RequestBody bookDto: BookRequestDto): BookResponseDto {
        log.info("Creating new book: {}", bookDto)
        
        val book = Book(
            name = bookDto.name,
            category = bookDto.category
        )

        // If author is provided, find or create the author
        bookDto.author?.let { authorDto ->
            val author = Author(
                name = authorDto.name,
                biography = authorDto.biography
            )
            book.author = author
        }

        return BookResponseDto.from(bookRepository.save(book))
            .also { log.debug("Created book with id: {}", it.id) }
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
    fun updateBook(@PathVariable id: Long, @RequestBody bookDto: BookRequestDto): ResponseEntity<BookResponseDto> {
        log.info("Updating book with id: {}", id)
        val existingBook = bookRepository.findById(id).orElseThrow {
            log.warn("Book not found with id: {}", id)
            throw BusinessException(ErrorCode.BOOK_NOT_FOUND)
        }

        existingBook.name = bookDto.name
        existingBook.category = bookDto.category

        // Update author if provided
        bookDto.author?.let { authorDto ->
            val author = existingBook.author ?: Author(
                name = authorDto.name,
                biography = authorDto.biography
            )
            author.name = authorDto.name
            author.biography = authorDto.biography
            existingBook.author = author
        }

        return ResponseEntity.ok(
            BookResponseDto.from(bookRepository.save(existingBook))
                .also { log.debug("Updated book: {}", it) }
        )
    }

    @Operation(summary = "Add a review to a book")
    @PostMapping("/{id}/reviews")
    fun addReview(@PathVariable id: Long, @RequestBody reviewDto: ReviewRequestDto): ResponseEntity<ReviewResponseDto> {
        log.info("Adding review to book with id: {}", id)
        val book = bookRepository.findById(id).orElseThrow {
            log.warn("Book not found with id: {}", id)
            throw BusinessException(ErrorCode.BOOK_NOT_FOUND)
        }

        val review = Review(
            rating = reviewDto.rating,
            comment = reviewDto.comment,
            book = book
        )

        return ResponseEntity.ok(
            ReviewResponseDto.from(reviewRepository.save(review))
        )
    }

    @Operation(summary = "Get all reviews for a book")
    @GetMapping("/{id}/reviews")
    fun getBookReviews(@PathVariable id: Long): ResponseEntity<List<ReviewResponseDto>> {
        log.info("Fetching reviews for book with id: {}", id)
        val book = bookRepository.findById(id).orElseThrow {
            log.warn("Book not found with id: {}", id)
            throw BusinessException(ErrorCode.BOOK_NOT_FOUND)
        }
        return ResponseEntity.ok(book.reviews.map { ReviewResponseDto.from(it) })
    }
}