package com.fndt.quote.domain.usecases

import com.fndt.quote.domain.QuotesFilter
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager

class SearchUseCase(
    private val query: String,
    private val filter: QuotesFilter,
    override val requestingUser: User,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Quote>>(requestManager) {
    override suspend fun makeRequest(): List<Quote> {
        return filter.apply {
            this.query = this@SearchUseCase.query
        }.getQuotes()
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasSearchPermission(requestingUser)
    }
}
