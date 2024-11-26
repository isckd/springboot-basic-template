package com.example.demo.entity

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.*

@Schema(description = "Book entity")
@Entity
@Table(name = "books")
class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var category: String,

    @ManyToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "author_id")
    var author: Author? = null,

    @OneToMany(mappedBy = "book", cascade = [CascadeType.ALL])
    var reviews: MutableList<Review> = mutableListOf()
)