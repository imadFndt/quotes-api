package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.repository.*
import com.fndt.quote.domain.services.RegularUserService

internal open class RegularUserServiceImpl(
    private val commentRepository: CommentRepository,
    private val quoteRepository: QuoteRepository,
    private val likeRepository: LikeRepository,
    private val tagRepository: TagRepository,
    private val userRepository: UserRepository,
    private val requestManager: RequestManager
) : RegularUserService {

    override suspend fun getQuotes(userId: Int?): List<Quote> = quoteRepository.getQuotes(userId, false, false, null)

    override suspend fun setQuoteLike(like: Like, likeAction: Boolean): Boolean = requestManager.execute {
        quoteRepository.findById(like.quoteId) ?: throw IllegalArgumentException("Quote does not exist")
        userRepository.findUser(like.userId) ?: throw IllegalArgumentException("User does not exist")
        val likeExists = likeRepository.find(like) != null
        when {
            likeAction && !likeExists -> likeRepository.like(like) != null
            !likeAction && likeExists -> likeRepository.unlike(like) != null
            else -> throw IllegalArgumentException("Like failed")
        }
    }

    override suspend fun addQuote(
        body: String,
        authorId: Int,
    ) = requestManager.execute {
        quoteRepository.insert(body, authorId) ?: run { throw IllegalStateException() }
    }

    override suspend fun getComments(quoteId: Int): List<Comment> = requestManager.execute {
        commentRepository.getComments(quoteId)
    }

    override suspend fun addComment(commentBody: String, quoteId: Int, userId: Int) = requestManager.execute {
        quoteRepository.findById(quoteId) ?: throw IllegalArgumentException("Quote does not exist")
        commentRepository.insert(commentBody, quoteId, userId)
    }
}
