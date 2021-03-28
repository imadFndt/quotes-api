package com.fndt.quote.domain.usecases

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.QuoteRepository

class SearchUseCase(
    private val query: String,
    private val quotesRepository: QuoteRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Quote>>(requestManager) {
    override suspend fun makeRequest(): List<Quote> {
        return quotesRepository.get(QuoteFilterArguments(query = query))
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(requestingUser)
    }
}
