package com.fndt.quote.domain.usecases.get

import com.fndt.quote.domain.dto.*
import com.fndt.quote.domain.filter.Access
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.manager.*
import com.fndt.quote.domain.repository.AuthorRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.TagRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase

const val AUTHOR_KEY = "author"
const val USER_KEY = "user"
const val TAG_KEY = "tag"
const val ORDER_KEY = "order"
const val ACCESS_KEY = "access"
const val QUERY_KEY = "query"
const val PAGE_KEY = "page"
const val PER_PAGE_KEY = "per_page"

class QuoteSelectionUseCase(
    private val arguments: Map<String, Any?>,
    repositoryProvider: RepositoryProvider,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<Quotes>(requestManager) {

    private val userRepository = repositoryProvider.getRepository<UserRepository>()
    private val authorRepository = repositoryProvider.getRepository<AuthorRepository>()
    private val tagRepository = repositoryProvider.getRepository<TagRepository>()
    private val quoteRepository = repositoryProvider.getRepository<QuoteRepository>()

    private lateinit var pager: ListPagerManager

    private var targetAuthor: Author? = null
    private var targetUser: User? = null
    private var targetTag: Tag? = null
    private var targetAccess: Access = Access.PUBLIC
    private var targetOrder: QuotesOrder = QuotesOrder.LATEST
    private var targetQuery: String? = null
    private var targetPage: Int = 1
    private var targetPerPage: Int = 20

    private val User.isRegularUsingPrivateData
        get() = role == AuthRole.REGULAR && (targetTag?.isPublic == false || targetAccess == Access.PRIVATE || targetAccess == Access.ALL)

    override fun onStartRequest() {
        arguments.interpret()
        pager = ListPagerManager(targetPage, targetPerPage)
    }

    override fun validate(user: User?): Boolean {
        return permissionManager.isAuthorized(requestingUser) && user?.isRegularUsingPrivateData == false
    }

    override suspend fun makeRequest(): Quotes {
        val filterArgs = QuoteFilterArguments(
            order = targetOrder,
            user = targetUser,
            authorId = targetAuthor?.id,
            tagId = targetTag?.id,
            query = targetQuery,
            quoteAccess = targetAccess,
            requestingUser = requestingUser,
        )
        quoteRepository.get(filterArgs).also {
            return Quotes(page = targetPage, totalPages = pager.getTotalPages(it), quotes = pager.getPaged(it))
        }
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
        targetAccess = this[ACCESS_KEY] as? Access ?: Access.PUBLIC
        targetQuery = this[QUERY_KEY] as? String
        targetPage = this[PAGE_KEY] as? Int ?: targetPage
        if (targetPage < 1) throw IllegalStateException("Bad page value")
        targetPerPage = this[PER_PAGE_KEY] as? Int ?: targetPerPage
        if (targetPerPage < 1) throw IllegalStateException("Bad per page value")
    }
}
