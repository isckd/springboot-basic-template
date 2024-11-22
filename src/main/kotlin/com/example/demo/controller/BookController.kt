package com.example.demo.controller

import com.example.demo.entity.Book
import com.example.demo.repository.BookRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Book", description = "Book management APIs")
@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookRepository: BookRepository
) {
    @Operation(summary = "Get all books")
    @GetMapping
    fun getAllBooks(): List<Book> = bookRepository.findAll()

    @Operation(
        summary = "Get a book by ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Book found"),
            ApiResponse(responseCode = "404", description = "Book not found")
        ]
    )
    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Long): ResponseEntity<Book> {
        return bookRepository.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @Operation(summary = "Create a new book")
    @PostMapping
    fun createBook(@RequestBody book: Book): Book = bookRepository.save(book)

    @Operation(
        summary = "Delete a book by ID",
        responses = [
            ApiResponse(responseCode = "204", description = "Book deleted"),
            ApiResponse(responseCode = "404", description = "Book not found")
        ]
    )
    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: Long): ResponseEntity<Void> {
        return if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id)
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
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
        return if (bookRepository.existsById(id)) {
            book.id = id
            ResponseEntity.ok(bookRepository.save(book))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @Operation(
        summary = "Partially update a book by ID",
        responses = [
            ApiResponse(responseCode = "200", description = "Book updated"),
            ApiResponse(responseCode = "404", description = "Book not found")
        ]
    )
    @PatchMapping("/{id}")
    fun partialUpdateBook(@PathVariable id: Long, @RequestBody updates: Map<String, Any>): ResponseEntity<Book> {
        return bookRepository.findById(id).map { existingBook ->
            updates.forEach { (key, value) ->
                when (key) {
                    "name" -> existingBook.name = value as String
                    "category" -> existingBook.category = value as String
                }
            }
            ResponseEntity.ok(bookRepository.save(existingBook))
        }.orElse(ResponseEntity.notFound().build())
    }
}
