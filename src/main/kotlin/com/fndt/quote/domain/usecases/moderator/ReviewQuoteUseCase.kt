package com.fndt.quote.domain.usecases.moderator

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class ReviewQuoteUseCase(
    private val quoteId: Int,
    private val decision: Boolean,
    private val quoteRepository: QuoteRepository,
    override val requestingUser: User?,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Quote>(requestManager) {
    override suspend fun makeRequest(): Quote {
        val quote = quoteRepository.findById(quoteId) ?: throw IllegalStateException("Quote not found")
        return if (decision) {
            val newQuote = quote.copy(isPublic = true)
            val id = quoteRepository.add(newQuote)
            quoteRepository.findById(id) ?: throw IllegalStateException("Quote not found")
        } else {
            quoteRepository.remove(quoteId)
            quote
        }
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasSetQuoteVisibilityPermission(user)
    }
}
