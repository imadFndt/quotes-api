package com.fndt.quote.domain.usecases.comments

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.dto.isBanned
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.CommentRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class AddCommentUseCase(
    private val body: String,
    private val quoteId: Int,
    private val user: User,
    private val commentRepository: CommentRepository,
    private val quoteRepository: QuoteRepository,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Comment>(requestManager) {

    override val requestingUser: User = user

    override suspend fun makeRequest(): Comment {
        quoteRepository.findById(quoteId) ?: throw IllegalStateException("Quote does not exist")
        val comment = Comment(body = body, quoteId = quoteId, createdAt = System.currentTimeMillis(), user = user)
        val id = commentRepository.add(comment)
        return commentRepository.findComment(id) ?: throw IllegalStateException("Failed to add comment")
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(requestingUser) && user?.isBanned == false
    }
}
