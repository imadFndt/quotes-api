package com.fndt.quote.domain.usecases.comments

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.CommentRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class GetCommentsUseCase(
    private val quoteId: Int,
    private val commentRepository: CommentRepository,
    override val requestingUser: User?,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Comment>>(requestManager) {
    override suspend fun makeRequest(): List<Comment> = commentRepository.get(quoteId)

    override fun validate(user: User?): Boolean {
        return permissionManager.hasGetCommentPermission(requestingUser)
    }
}
