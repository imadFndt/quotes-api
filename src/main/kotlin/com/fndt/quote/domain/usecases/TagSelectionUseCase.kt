package com.fndt.quote.domain.usecases

import com.fndt.quote.domain.PermissionException
import com.fndt.quote.domain.QuotesFilter
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.TagRepository

class TagSelectionUseCase(
    private val tagId: Int,
    private val filter: QuotesFilter,
    private val tagRepository: TagRepository,
    override val requestingUser: User,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Quote>>(requestManager) {

    override suspend fun makeRequest(): List<Quote> {
        val tag = tagRepository.findById(tagId) ?: throw IllegalStateException("Tag not found")
        val access = if (requestingUser.role == AuthRole.REGULAR) true else null
        if (!tag.isPublic && requestingUser.role == AuthRole.REGULAR) throw PermissionException("Permission denied")
        return filter.apply {
            this.tag = tag
            isPublic = access
            orderPopulars = true
        }.getQuotes()
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasTagSelectionsPermission(user)
    }
}
