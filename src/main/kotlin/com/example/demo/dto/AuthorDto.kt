package com.example.demo.dto

import io.swagger.v3.oas.annotations.media.Schema
import com.example.demo.entity.Author

data class AuthorRequestDto(
    @Schema(description = "Author name", example = "JinUk Ye")
    val name: String,

    @Schema(description = "Author biography", example = "English writer and philologist")
    val biography: String? = null
)

data class AuthorResponseDto(
    @Schema(description = "Author ID", example = "1")
    val id: Long,

    @Schema(description = "Author name", example = "JinUk Ye")
    val name: String,

    @Schema(description = "Author biography", example = "English writer and philologist")
    val biography: String? = null
) {
    companion object {
        fun from(author: Author): AuthorResponseDto {
            return AuthorResponseDto(
                id = author.id!!,
                name = author.name,
                biography = author.biography
            )
        }
    }
}
