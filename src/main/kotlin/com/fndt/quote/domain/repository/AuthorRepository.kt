package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.Author
import com.fndt.quote.domain.repository.base.SimpleRepository

interface AuthorRepository : SimpleRepository<Author> {
    fun get(): List<Author>
    fun findByName(name: String): Author?
}
