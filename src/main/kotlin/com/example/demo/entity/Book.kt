package com.example.demo.entity

import jakarta.persistence.*
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Book entity")
@Entity
@Table(name = "books")
class Book(
    @Schema(description = "Book ID", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    @Schema(description = "Book name", example = "The Lord of the Rings")
    @Column(nullable = false)
    var name: String,

    @Schema(description = "Book category", example = "Fantasy")
    @Column(nullable = false)
    var category: String
)
