package com.fndt.quote.domain.usecases.selections

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.QuoteRepository

@Deprecated("Deprecated to complex filter")
class SearchUseCase(
    private val query: String,
    quotesRepository: QuoteRepository,
    override val requestingUser: User,
    permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : BaseSelectionUseCase(quotesRepository, requestingUser, permissionManager, requestManager) {
    override fun getArguments() = QuoteFilterArguments(query = query)
}
