package com.fndt.quote.domain

import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote

interface QuotesBrowseService {
    suspend fun getQuotes(id: Int? = null): List<Quote>
    suspend fun setQuoteLike(like: Like, login: String): Boolean
    suspend fun getComments(quoteId: Int): List<Comment>
    suspend fun addComment(commentBody: String, quoteId: Int, userName: String): Boolean
    suspend fun deleteComment(commentId: Int, userName: String): Boolean
}
