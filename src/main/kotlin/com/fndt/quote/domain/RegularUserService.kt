package com.fndt.quote.domain

import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote

interface RegularUserService {
    suspend fun getQuotes(id: Int? = null): List<Quote>
    suspend fun setQuoteLike(like: Like, userId: Int): Boolean
    suspend fun getComments(quoteId: Int): List<Comment>
    suspend fun addComment(commentBody: String, quoteId: Int, userId: Int): Boolean
    suspend fun deleteComment(commentId: Int, userId: Int): Boolean
    suspend fun upsertQuote(body: String, authorId: Int, tagId: List<Int>, quoteId: Int? = null): Boolean
    suspend fun removeQuote(quoteId: Int): Boolean
}
