package com.fndt.quote.domain.usecases.selections

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Author
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesAccess
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.AuthorRepository
import com.fndt.quote.domain.repository.QuoteRepository

@Deprecated(
    "Deprecated to complex filter",
    replaceWith = ReplaceWith("SelectionUseCase")
)
class AuthorSelectionUseCase(
    private val authorId: Int,
    quoteRepository: QuoteRepository,
    private val authorRepository: AuthorRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : BaseSelectionUseCase(quoteRepository, requestingUser, permissionManager, requestManager) {

    lateinit var targetAuthor: Author
    lateinit var targetAccess: QuotesAccess

    override fun getArguments(): QuoteFilterArguments {
        return QuoteFilterArguments(authorId = targetAuthor.id, access = targetAccess, order = QuotesOrder.LATEST)
    }

    override fun validate(user: User?): Boolean {
        targetAuthor = authorRepository.findById(authorId) ?: throw IllegalStateException("Author not found")
        targetAccess = if (requestingUser.role == AuthRole.REGULAR) QuotesAccess.PUBLIC else QuotesAccess.ALL
        return permissionManager.isAuthorized(user)
    }
}
