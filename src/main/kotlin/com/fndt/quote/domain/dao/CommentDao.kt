package com.fndt.quote.domain.dao

import com.fndt.quote.domain.dto.Comment

interface CommentDao {
    fun getComments(quoteId: Int): List<Comment>
    fun upsertComment(commentBody: String, quoteId: Int, userId: Int): Int
    fun deleteComment(commentId: Int): Int
    fun findComment(commentId: Int): Comment?
}
