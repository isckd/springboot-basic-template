package com.example.demo.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime
import com.example.demo.entity.Review

data class ReviewRequestDto(
    @Schema(description = "Review rating", example = "5")
    val rating: Int,

    @Schema(description = "Review comment", example = "Great book!")
    val comment: String? = null
)

data class ReviewResponseDto(
    @Schema(description = "Review ID", example = "1")
    val id: Long,

    @Schema(description = "Review rating", example = "5")
    val rating: Int,

    @Schema(description = "Review comment", example = "Great book!")
    val comment: String? = null,

    @Schema(description = "Review date")
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(review: Review): ReviewResponseDto {
            return ReviewResponseDto(
                id = review.id!!,
                rating = review.rating,
                comment = review.comment,
                createdAt = review.createdAt
            )
        }
    }
}
