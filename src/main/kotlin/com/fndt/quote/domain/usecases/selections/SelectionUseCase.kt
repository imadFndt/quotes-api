package com.fndt.quote.domain.usecases.selections

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.*
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesAccess
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.repository.AuthorRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.RequestUseCase
import kotlin.math.ceil

const val AUTHOR_KEY = "author"
const val USER_KEY = "user"
const val TAG_KEY = "tag"
const val ORDER_KEY = "order"
const val ACCESS_KEY = "access"
const val QUERY_KEY = "query"
const val PAGE_KEY = "page"
const val PER_PAGE_KEY = "per_page"

class SelectionUseCase(
    private val arguments: Map<String, Any?>,
    private val userRepository: UserRepository,
    private val authorRepository: AuthorRepository,
    private val tagRepository: TagRepository,
    private val quoteRepository: QuoteRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<Quotes>(requestManager) {

    private var targetAuthor: Author? = null
    private var targetUser: User? = null
    private var targetTag: Tag? = null
    private var targetAccess: QuotesAccess = QuotesAccess.PUBLIC
    private var targetOrder: QuotesOrder = QuotesOrder.LATEST
    private var targetQuery: String? = null
    private var targetPage: Int = 1
    private var targetPerPage: Int = 20

    private val User.isRegularUsingPrivateData
        get() = role == AuthRole.REGULAR && (
            targetTag?.isPublic == false || targetAccess == QuotesAccess.PRIVATE || targetAccess == QuotesAccess.ALL
            )

    override suspend fun makeRequest(): Quotes {
        val filterArgs = QuoteFilterArguments(
            order = targetOrder,
            user = targetUser,
            authorId = targetAuthor?.id,
            tagId = targetTag?.id,
            query = targetQuery,
            access = targetAccess,
        )
        quoteRepository.get(filterArgs).also {
            return it.toPaged()
        }
    }

    override fun validate(user: User?): Boolean {
        arguments.interpret()
        return permissionManager.isAuthorized(requestingUser) && user?.isRegularUsingPrivateData == false
    }

    private fun Map<String, Any?>.interpret() {
        targetAuthor = (this[AUTHOR_KEY] as? ID)?.let { id ->
            authorRepository.findById(id) ?: throw IllegalStateException("Author not found")
        }
        targetUser = (this[USER_KEY] as? ID)?.let { id ->
            userRepository.findUserByParams(id) ?: throw IllegalStateException("User not found")
        }
        targetTag = (this[TAG_KEY] as? ID)?.let { id ->
            tagRepository.findById(id) ?: throw IllegalStateException("Tag not found")
        }
        targetOrder = this[ORDER_KEY] as? QuotesOrder ?: QuotesOrder.LATEST
        targetAccess = this[ACCESS_KEY] as? QuotesAccess ?: QuotesAccess.PUBLIC
        targetQuery = this[QUERY_KEY] as? String
        targetPage = this[PAGE_KEY] as? Int ?: targetPage
        if (targetPage < 1) throw IllegalStateException("Bad page value")
        targetPerPage = this[PER_PAGE_KEY] as? Int ?: targetPerPage
        if (targetPerPage < 1) throw IllegalStateException("Bad per page value")
    }

    private fun List<Quote>.toPaged(): Quotes {
        val startIndex = targetPerPage * (targetPage - 1)
        val endIndex =
            (startIndex + targetPerPage).let { if (it > size) size - 1 else it }
        val pagedList = subList(startIndex, endIndex)
        val pages = ceil(size.toDouble() / targetPerPage).toInt()
        return Quotes(page = targetPage, totalPages = pages, quotes = pagedList)
    }
}
