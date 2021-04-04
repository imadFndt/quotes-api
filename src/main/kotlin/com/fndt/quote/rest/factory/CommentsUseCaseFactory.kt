package com.fndt.quote.rest.factory

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.CommentRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.base.UseCase
import com.fndt.quote.domain.usecases.comments.AddCommentUseCase
import com.fndt.quote.domain.usecases.comments.GetCommentsUseCase

class CommentsUseCaseFactory(
    private val commentRepository: CommentRepository,
    private val quoteRepository: QuoteRepository,
    private val requestManager: RequestManager,
    private val permissionManager: UserPermissionManager,
) {
    fun getCommentsUseCase(quoteId: Int, userRequesting: User): UseCase<List<Comment>> {
        return GetCommentsUseCase(
            quoteId, quoteRepository, commentRepository, userRequesting, permissionManager, requestManager
        )
    }

    fun addCommentsUseCase(body: String, quoteId: ID, user: User): UseCase<Comment> {
        return AddCommentUseCase(
            body, quoteId, user, commentRepository, quoteRepository, permissionManager, requestManager
        )
    }
}
