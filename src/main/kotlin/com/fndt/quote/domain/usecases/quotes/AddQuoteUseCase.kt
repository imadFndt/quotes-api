package com.fndt.quote.domain.usecases.quotes

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class AddQuoteUseCase(
    private val body: String,
    private val authorId: Int,
    private val quoteRepository: QuoteRepository,
    override val requestingUser: User?,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Quote>(requestManager) {
    override suspend fun makeRequest(): Quote {
        return quoteRepository.insert(body, authorId) ?: throw IllegalStateException()
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasAddQuotePermission(user)
    }
}
