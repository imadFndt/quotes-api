package com.fndt.quote.domain.services

import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote

interface RegularUserService {
    suspend fun getQuotes(userId: Int? = null): List<Quote>
    suspend fun addQuote(body: String, authorId: Int): Quote
    suspend fun setQuoteLike(like: Like, likeAction: Boolean): Boolean

    suspend fun getComments(quoteId: Int): List<Comment>
    suspend fun addComment(commentBody: String, quoteId: Int, userId: Int): Comment?
}
