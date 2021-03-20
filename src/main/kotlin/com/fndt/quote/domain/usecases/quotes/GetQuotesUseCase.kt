package com.fndt.quote.domain.usecases.quotes

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class GetQuotesUseCase(
    private val userId: Int? = null,
    private val isPublic: Boolean? = null,
    private val orderPopulars: Boolean = false,
    private val tagId: Int? = null,
    private val quoteRepository: QuoteRepository,
    override val requestingUser: User?,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Quote>>(requestManager) {
    override suspend fun makeRequest(): List<Quote> {
        return quoteRepository.getQuotes(userId, isPublic, orderPopulars, tagId)
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasGetQuotesPermission(requestingUser)
    }
}
