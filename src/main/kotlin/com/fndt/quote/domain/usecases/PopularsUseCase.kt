package com.fndt.quote.domain.usecases

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.QuoteRepository

class PopularsUseCase(
    private val quoteRepository: QuoteRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Quote>>(requestManager) {
    override suspend fun makeRequest(): List<Quote> {
        return quoteRepository.get(QuoteFilterArguments(order = QuotesOrder.POPULARS))
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(requestingUser)
    }
}
