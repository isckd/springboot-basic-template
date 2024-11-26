package com.example.demo.repository

import com.example.demo.entity.Author
import org.springframework.data.jpa.repository.JpaRepository

interface AuthorRepository : JpaRepository<Author, Long>