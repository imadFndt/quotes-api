package com.fndt.quote.domain.dao

import com.fndt.quote.domain.dto.Comment

interface CommentDao {
    fun getComments(quoteId: Int): List<Comment>
    fun insert(commentBody: String, quoteId: Int, userId: Int): Comment?
    fun remove(commentId: Int): Int
    fun findComment(commentId: Int): Comment?
}
