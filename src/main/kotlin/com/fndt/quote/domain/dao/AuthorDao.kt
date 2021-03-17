package com.fndt.quote.domain.dao

import com.fndt.quote.domain.dto.Author

interface AuthorDao {
    fun addAuthor(name: String): Author?
    fun updateAuthor(authorId: Int, name: String): Author?
    fun removeAuthor(id: Int): Int
    fun findById(id: Int): Author?
}
