package com.fndt.quote.domain.usecases.quotes

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesAccess
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase

class GetQuotesUseCase(
    private val searchUserId: Int? = null,
    private val userRepository: UserRepository,
    private val quoteRepository: QuoteRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Quote>>(requestManager) {
    override suspend fun makeRequest(): List<Quote> {
        val user = searchUserId?.let {
            userRepository.findUserByParams(userId = searchUserId) ?: throw IllegalStateException("User not found")
        }
        val access = if (requestingUser.role == AuthRole.REGULAR) QuotesAccess.PUBLIC else QuotesAccess.ALL
        return quoteRepository.get(QuoteFilterArguments(access = access, user = user))
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(requestingUser)
    }
}
