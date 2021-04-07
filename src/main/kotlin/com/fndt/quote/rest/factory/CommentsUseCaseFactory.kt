package com.fndt.quote.rest.factory

import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.ID
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.AddAdapterProvider
import com.fndt.quote.domain.manager.RepositoryProvider
import com.fndt.quote.domain.manager.RequestManager
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.usecases.add.AddUseCase
import com.fndt.quote.domain.usecases.base.UseCase
import com.fndt.quote.domain.usecases.get.GetCommentsUseCase

class CommentsUseCaseFactory(
    private val requestManager: RequestManager,
    private val repositoryProvider: RepositoryProvider,
    private val permissionManager: UserPermissionManager,
    private val addAdapterProvider: AddAdapterProvider,
) {
    fun getCommentsUseCase(quoteId: Int, userRequesting: User): UseCase<List<Comment>> {
        return GetCommentsUseCase(quoteId, repositoryProvider, userRequesting, permissionManager, requestManager)
    }

    fun addCommentsUseCase(body: String, quoteId: ID, user: User): UseCase<Unit> {
        val adapter = addAdapterProvider.createAddCommentAdapter(body, quoteId)
        return AddUseCase(adapter, user, requestManager)
    }
}
