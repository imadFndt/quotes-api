package com.fndt.quote.domain.dao

import com.fndt.quote.domain.dto.Author

interface AuthorDao {
    fun findById(id: Int): Author?
}
