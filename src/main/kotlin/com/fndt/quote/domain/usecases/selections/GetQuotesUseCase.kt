package com.fndt.quote.domain.usecases.selections

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesAccess
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.UserRepository

class GetQuotesUseCase(
    private val searchUserId: Int? = null,
    private val userRepository: UserRepository,
    quoteRepository: QuoteRepository,
    override val requestingUser: User,
    permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : BaseSelectionUseCase(quoteRepository, requestingUser, permissionManager, requestManager) {

    override fun getArguments(): QuoteFilterArguments {
        val user = searchUserId?.let {
            userRepository.findUserByParams(userId = searchUserId) ?: throw IllegalStateException("User not found")
        }
        val access = if (requestingUser.role == AuthRole.REGULAR) QuotesAccess.PUBLIC else QuotesAccess.ALL
        return QuoteFilterArguments(access = access, user = user)
    }
}
