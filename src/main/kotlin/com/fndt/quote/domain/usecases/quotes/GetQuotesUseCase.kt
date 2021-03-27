package com.fndt.quote.domain.usecases.quotes

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesAccess
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class GetQuotesUseCase(
    private val searchUser: User? = null,
    private val quoteRepository: QuoteRepository,
    override val requestingUser: User,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Quote>>(requestManager) {
    override suspend fun makeRequest(): List<Quote> {
        val access = if (requestingUser.role == AuthRole.REGULAR) QuotesAccess.PUBLIC else QuotesAccess.ALL
        return quoteRepository.get(QuoteFilterArguments(access = access, user = searchUser))
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasGetQuotesPermission(requestingUser)
    }
}
