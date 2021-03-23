package com.fndt.quote.domain.usecases.quotes

import com.fndt.quote.domain.QuotesFilter
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.usecases.RequestUseCase

class GetQuotesUseCase(
    private val searchUser: User? = null,
    private val filterBuilder: QuotesFilter,
    override val requestingUser: User,
    private val permissionManager: PermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Quote>>(requestManager) {
    override suspend fun makeRequest(): List<Quote> {
        val access = if (requestingUser.role == AuthRole.REGULAR) true else null
        return filterBuilder.apply {
            isPublic = access
            user = searchUser
        }.getQuotes()
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.hasGetQuotesPermission(requestingUser)
    }
}
