package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.repository.base.SimpleRepository

interface CommentRepository : SimpleRepository<Comment> {
    fun get(quoteId: ID): List<Comment>
}
