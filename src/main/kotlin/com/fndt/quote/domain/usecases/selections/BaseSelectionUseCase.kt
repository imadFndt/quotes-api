package com.fndt.quote.domain.usecases.selections

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.RequestUseCase

abstract class BaseSelectionUseCase(
    private val quoteRepository: QuoteRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<List<Quote>>(requestManager) {

    abstract fun getArguments(): QuoteFilterArguments

    final override suspend fun makeRequest(): List<Quote> {
        return quoteRepository.get(getArguments())
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(requestingUser)
    }
}
