package com.fndt.quote.domain.usecases.comments

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.CommentRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase

class GetCommentsUseCase(
    private val quoteId: Int,
    private val quoteRepository: QuoteRepository,
    private val commentRepository: CommentRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Comment>>(requestManager) {

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(requestingUser)
    }

    override suspend fun makeRequest(): List<Comment> {
        val quote = quoteRepository.findById(quoteId) ?: throw IllegalStateException("Quote not exists")
        return commentRepository.get(quote.id)
    }
}
