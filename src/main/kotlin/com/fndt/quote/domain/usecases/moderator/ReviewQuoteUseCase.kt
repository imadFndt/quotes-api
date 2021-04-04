package com.fndt.quote.domain.usecases.moderator

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class ReviewQuoteUseCase(
    private val quoteId: Int,
    private val decision: Boolean,
    private val quoteRepository: QuoteRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Unit>(requestManager) {

    override fun validate(user: User?): Boolean {
        return permissionManager.hasModeratorPermission(user)
    }

    override suspend fun makeRequest() {
        val quote = quoteRepository.findById(quoteId) ?: throw IllegalStateException("Quote not found")
        if (decision) {
            val newQuote = quote.copy(isPublic = true)
            quoteRepository.add(newQuote)
        } else {
            quoteRepository.remove(quoteId)
        }
    }
}
