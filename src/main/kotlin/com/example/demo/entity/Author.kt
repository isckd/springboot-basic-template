package com.example.demo.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

@Schema(description = "Author entity")
@Entity
@Table(name = "authors")
class Author(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var biography: String? = null,

    @OneToMany(mappedBy = "author", cascade = [CascadeType.ALL])
    var books: MutableList<Book> = mutableListOf()
)