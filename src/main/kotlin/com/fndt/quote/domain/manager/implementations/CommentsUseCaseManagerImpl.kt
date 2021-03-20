package com.fndt.quote.domain.manager.implementations

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.CommentsUseCaseManager
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.CommentRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.UseCase
import com.fndt.quote.domain.usecases.comments.AddCommentUseCase
import com.fndt.quote.domain.usecases.comments.GetCommentsUseCase

typealias PermissionException = IllegalStateException

class CommentsUseCaseManagerImpl(
    private val commentRepository: CommentRepository,
    private val quoteRepository: QuoteRepository,
    private val requestManager: RequestManager,
    private val permissionManager: PermissionManager,
) : CommentsUseCaseManager {
    override fun getCommentsUseCase(quoteId: Int, userRequesting: User?): UseCase<List<Comment>> {
        return GetCommentsUseCase(quoteId, commentRepository, userRequesting, permissionManager, requestManager)
    }

    override fun addCommentsUseCase(body: String, quoteId: Int, userRequesting: User): UseCase<Comment> {
        return AddCommentUseCase(
            body,
            quoteId,
            userRequesting.id,
            commentRepository,
            quoteRepository,
            userRequesting,
            permissionManager,
            requestManager
        )
    }
}
