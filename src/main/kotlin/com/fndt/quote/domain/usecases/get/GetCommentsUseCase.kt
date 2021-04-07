package com.fndt.quote.domain.usecases.get

import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.RepositoryProvider
import com.fndt.quote.domain.manager.RequestManager
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.manager.getRepository
import com.fndt.quote.domain.repository.CommentRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase

class GetCommentsUseCase(
    private val quoteId: Int,
    repositoryProvider: RepositoryProvider,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Comment>>(requestManager) {

    private val quoteRepository = repositoryProvider.getRepository<QuoteRepository>()
    private val commentRepository = repositoryProvider.getRepository<CommentRepository>()

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(requestingUser)
    }

    override suspend fun makeRequest(): List<Comment> {
        val quote = quoteRepository.findById(quoteId) ?: throw IllegalStateException("Quote not exists")
        return commentRepository.get(quote.id)
    }
}
