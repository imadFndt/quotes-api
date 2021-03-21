package com.fndt.quote.domain.usecases

import com.fndt.quote.controllers.factory.PermissionException
import com.fndt.quote.domain.QuoteFilter
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.TagRepository

class TagSelectionUseCase(
    private val tagId: Int,
    private val filterBuilder: QuoteFilter.Builder,
    private val tagRepository: TagRepository,
    override val requestingUser: User,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Quote>>(requestManager) {

    override suspend fun makeRequest(): List<Quote> {
        val tag = tagRepository.findById(tagId) ?: throw IllegalStateException("Tag not found")
        val access = if (requestingUser.role == AuthRole.REGULAR) true else null
        if (!tag.isPublic && requestingUser.role == AuthRole.REGULAR) throw PermissionException("Permission denied")
        return filterBuilder.setTag(tag).setAccess(access).setOrderPopulars(true).build()
            .getQuotes()
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasTagSelectionsPermission(user)
    }
}
