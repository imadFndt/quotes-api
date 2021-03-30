package com.fndt.quote.domain.usecases.selections

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesAccess
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.TagRepository

@Deprecated("Deprecated to complex filter")
class TagSelectionUseCase(
    private val tagId: Int,
    quoteRepository: QuoteRepository,
    private val tagRepository: TagRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : BaseSelectionUseCase(quoteRepository, requestingUser, permissionManager, requestManager) {

    lateinit var targetTag: Tag
    lateinit var targetAccess: QuotesAccess

    private val isRegularAccessPrivateTag: Boolean
        get() = !targetTag.isPublic && requestingUser.role == AuthRole.REGULAR

    override fun getArguments(): QuoteFilterArguments {
        return QuoteFilterArguments(tagId = targetTag.id, access = targetAccess)
    }

    override fun validate(user: User?): Boolean {
        targetTag = tagRepository.findById(tagId) ?: throw IllegalStateException("Tag not found")
        targetAccess = if (requestingUser.role == AuthRole.REGULAR) QuotesAccess.PUBLIC else QuotesAccess.ALL
        return permissionManager.isAuthorized(user) && !isRegularAccessPrivateTag
    }
}
