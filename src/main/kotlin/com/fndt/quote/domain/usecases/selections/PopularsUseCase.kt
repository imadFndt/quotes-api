package com.fndt.quote.domain.usecases.selections

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.QuoteRepository

@Deprecated("Deprecated to complex filter")
class PopularsUseCase(
    quoteRepository: QuoteRepository,
    override val requestingUser: User,
    permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : BaseSelectionUseCase(quoteRepository, requestingUser, permissionManager, requestManager) {
    override fun getArguments() = QuoteFilterArguments(order = QuotesOrder.POPULARS)
}
