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

const val AUTHOR_KEY = "author"
const val USER_KEY = "user"
const val TAG_KEY = "tag"
const val ORDER_KEY = "order"
const val ACCESS_KEY = "access"
const val QUERY_KEY = "query"

class SelectionUseCase(
    private val arguments: Map<String, Any?>,
    private val userRepository: UserRepository,
    private val authorRepository: AuthorRepository,
    private val tagRepository: TagRepository,
    private val quoteRepository: QuoteRepository,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager,
) : RequestUseCase<List<Quote>>(requestManager) {

    private var targetAuthor: Author? = null
    private var targetUser: User? = null
    private var targetTag: Tag? = null
    private var targetAccess: QuotesAccess = QuotesAccess.PUBLIC
    private var targetOrder: QuotesOrder = QuotesOrder.LATEST
    private var targetQuery: String? = null

    private val User.isRegularUsingPrivateData
        get() = role == AuthRole.REGULAR && (
            targetTag?.isPublic == false || targetAccess == QuotesAccess.PRIVATE || targetAccess == QuotesAccess.ALL
            )

    override suspend fun makeRequest(): List<Quote> {
        val filterArgs = QuoteFilterArguments(
            order = targetOrder,
            user = targetUser,
            authorId = targetAuthor?.id,
            tagId = targetTag?.id,
            query = targetQuery,
            access = targetAccess
        ).apply {
            order = targetOrder
            this.user = targetUser
            this.authorId = targetAuthor?.id
            this.tagId = targetTag?.id
        }
        return quoteRepository.get(filterArgs)
    }

    override fun validate(user: User?): Boolean {
        arguments.interpret()
        return permissionManager.isAuthorized(requestingUser) && user?.isRegularUsingPrivateData == false
    }

    private fun Map<String, Any?>.interpret() {
        targetAuthor = (this["author"] as? ID)?.let { id ->
            authorRepository.findById(id) ?: throw IllegalStateException("Author not found")
        }
        targetUser = (this["user"] as? ID)?.let { id ->
            userRepository.findUserByParams(id) ?: throw IllegalStateException("User not found")
        }
        targetTag = (this["tag"] as? ID)?.let { id ->
            tagRepository.findById(id) ?: throw IllegalStateException("Tag not found")
        }
        targetOrder = this["order"] as? QuotesOrder ?: QuotesOrder.LATEST
        targetAccess = this["access"] as? QuotesAccess ?: QuotesAccess.PUBLIC
        targetQuery = this["query"] as? String
    }
}
