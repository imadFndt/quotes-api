package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.Author
import com.fndt.quote.domain.dto.ID

interface AuthorRepository {
    fun get(): List<Author>
    fun add(author: Author): ID
    fun remove(author: Author): Int
    fun findById(authorId: ID): Author?
    fun findByName(name: String): Author?
}
