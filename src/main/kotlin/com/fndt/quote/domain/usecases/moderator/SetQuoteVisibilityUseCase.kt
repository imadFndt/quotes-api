package com.fndt.quote.domain.usecases.moderator

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class SetQuoteVisibilityUseCase(
    private val quoteId: Int,
    private val isPublic: Boolean,
    private val quoteRepository: QuoteRepository,
    override val requestingUser: User?,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Quote>(requestManager) {
    override suspend fun makeRequest(): Quote {
        quoteRepository.findById(quoteId) ?: throw IllegalStateException("Quote not found")
        return quoteRepository.update(quoteId, isPublic = isPublic) ?: throw IllegalStateException("Update failed")
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasSetQuoteVisibilityPermission(user)
    }
}
