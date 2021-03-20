package com.fndt.quote.domain.usecases.comments

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.CommentRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class AddCommentUseCase(
    private val body: String,
    private val quoteId: Int,
    private val userId: Int,
    private val commentRepository: CommentRepository,
    private val quoteRepository: QuoteRepository,
    override val requestingUser: User?,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Comment>(requestManager) {
    override suspend fun makeRequest(): Comment {
        quoteRepository.findById(quoteId) ?: throw IllegalArgumentException("Quote does not exist")
        return commentRepository.insert(body, quoteId, userId)
            ?: throw IllegalArgumentException("Comment not found")
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasAddCommentPermission(requestingUser)
    }
}
