package com.example.demo.dto

import io.swagger.v3.oas.annotations.media.Schema
import com.example.demo.entity.Book

data class BookRequestDto(
    @Schema(description = "Book name", example = "The Lord of the Rings")
    val name: String,

    @Schema(description = "Book category", example = "Fantasy")
    val category: String,

    @Schema(description = "Author information")
    val author: AuthorRequestDto? = null
)

data class BookResponseDto(
    @Schema(description = "Book ID", example = "1")
    val id: Long,

    @Schema(description = "Book name", example = "The Lord of the Rings")
    val name: String,

    @Schema(description = "Book category", example = "Fantasy")
    val category: String,

    @Schema(description = "Author information")
    val author: AuthorResponseDto? = null,

    @Schema(description = "Book reviews")
    val reviews: List<ReviewResponseDto> = emptyList()
) {
    companion object {
        fun from(book: Book): BookResponseDto {
            return BookResponseDto(
                id = book.id!!,
                name = book.name,
                category = book.category,
                author = book.author?.let { AuthorResponseDto.from(it) },
                reviews = book.reviews.map { ReviewResponseDto.from(it) }
            )
        }
    }
}
