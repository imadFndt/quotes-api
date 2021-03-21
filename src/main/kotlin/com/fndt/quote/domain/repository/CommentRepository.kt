package com.fndt.quote.domain.repository

import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.ID

interface CommentRepository {
    fun getComments(quoteId: Int): List<Comment>
    fun add(comment: Comment): ID
    fun remove(comment: Comment)
    fun findComment(commentId: Int): Comment?
}