package com.fndt.quote.domain.usecases

import com.fndt.quote.domain.PermissionException
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesAccess
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.TagRepository

class TagSelectionUseCase(
    private val tagId: Int,
    private val quoteRepository: QuoteRepository,
    private val tagRepository: TagRepository,
    override val requestingUser: User,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Quote>>(requestManager) {

    override suspend fun makeRequest(): List<Quote> {
        val tag = tagRepository.findById(tagId) ?: throw IllegalStateException("Tag not found")
        val access = if (requestingUser.role == AuthRole.REGULAR) QuotesAccess.PUBLIC else QuotesAccess.ALL
        if (!tag.isPublic && requestingUser.role == AuthRole.REGULAR) throw PermissionException("Permission denied")
        return quoteRepository.get(QuoteFilterArguments(tag = tag, access = access, order = QuotesOrder.LATEST))
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasTagSelectionsPermission(user)
    }
}
