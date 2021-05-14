package com.fndt.quote.rest.factory

import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.Access
import com.fndt.quote.domain.manager.*
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.usecases.add.AddUseCase
import com.fndt.quote.domain.usecases.base.UseCase
import com.fndt.quote.domain.usecases.get.GetTagsUseCase
import com.fndt.quote.domain.usecases.review.ReviewUseCase
import com.fndt.quote.domain.usecases.review.SimpleReviewUseCaseAdapter

class TagsUseCaseFactory(
    private val addAdapterProvider: AddAdapterProvider,
    private val repositoryProvider: RepositoryProvider,
    private val permissionManager: UserPermissionManager,
    private val requestManager: RequestManager
) {
    fun getTagsUseCase(accessParameter: Access?, user: User): UseCase<List<Tag>> = GetTagsUseCase(
        access = accessParameter ?: GetTagsUseCase.DEFAULT_ACCESS,
        tagRepository = repositoryProvider.getRepository(),
        requestingUser = user,
        permissionManager = permissionManager,
        requestManager = requestManager
    )

    fun getAddTagUseCase(tagName: String, requestingUser: User): UseCase<Unit> {
        val adapter = addAdapterProvider.createAddTagAdapter(tagName)
        return AddUseCase(adapter, requestingUser, requestManager)
    }

    fun getAddQuoteToTagUseCase(quoteId: Int, tagId: Int, requestingUser: User): UseCase<Unit> {
        val adapter = addAdapterProvider.createAddQuoteToTagAdapter(quoteId, tagId)
        return AddUseCase(adapter, requestingUser, requestManager)
    }

    fun getApproveTagUseCase(tagId: Int, decision: Boolean, requestingUser: User): UseCase<Unit> {
        val tagRepository = repositoryProvider.getRepository<TagRepository>()
        val adapter = SimpleReviewUseCaseAdapter.createTagReviewAdapter(tagId, tagRepository, permissionManager)
        return ReviewUseCase(decision, adapter, requestingUser, requestManager)
    }
}
