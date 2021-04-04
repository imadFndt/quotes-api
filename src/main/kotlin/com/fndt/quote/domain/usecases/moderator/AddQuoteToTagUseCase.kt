package com.fndt.quote.domain.usecases.moderator

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.repository.TagSelectionRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase

class AddQuoteToTagUseCase(
    private val quoteId: Int,
    private val tagId: Int,
    private val tagRepository: TagRepository,
    private val quoteRepository: QuoteRepository,
    private val tagSelectionRepository: TagSelectionRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Unit>(requestManager) {

    override fun validate(user: User?): Boolean {
        return permissionManager.hasModeratorPermission(user)
    }

    override suspend fun makeRequest() {
        val quote = quoteRepository.findById(quoteId) ?: throw IllegalStateException("Quote not exist")
        val tag = tagRepository.findById(tagId) ?: throw IllegalStateException("Tag not exist")
        tagSelectionRepository.add(quote, tag)
    }
}
