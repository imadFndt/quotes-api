package com.fndt.quote.domain.services

import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote

interface RegularUserService {
    suspend fun getQuotes(id: Int? = null): List<Quote>
    suspend fun setQuoteLike(like: Like, likeAction: Boolean): Boolean
    suspend fun upsertQuote(body: String, authorId: Int, tagId: List<Int>, quoteId: Int? = null): Boolean
    suspend fun removeQuote(quoteId: Int): Boolean
    suspend fun getComments(quoteId: Int): List<Comment>
    suspend fun addComment(commentBody: String, quoteId: Int, userId: Int): Boolean
    suspend fun deleteComment(commentId: Int, userId: Int): Boolean
}
