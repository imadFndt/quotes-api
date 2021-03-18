package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.dao.*
import com.fndt.quote.domain.dto.Comment
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.services.RegularUserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*

internal open class RegularUserServiceImpl(
    private val commentDao: CommentDao,
    private val quoteDao: QuoteDao,
    private val likeDao: LikeDao,
    private val tagDao: TagDao,
    private val userDao: UserDao,
) : RegularUserService {

    override suspend fun getQuotes(id: Int?): List<Quote> = quoteDao.getQuotes(id, true)

    override suspend fun setQuoteLike(like: Like, likeAction: Boolean): Boolean = withContext(Dispatchers.IO) {
        quoteDao.findById(like.quoteId) ?: throw IllegalArgumentException("Quote does not exist")
        userDao.findUser(like.userId) ?: throw IllegalArgumentException("User does not exist")
        val likeExists = likeDao.find(like) != null
        when {
            likeAction && !likeExists -> likeDao.like(like) != null
            !likeAction && likeExists -> likeDao.unlike(like) > 0
            else -> return@withContext false
        }
    }

    override suspend fun addQuote(
        body: String,
        authorId: Int,
    ) = withContext(Dispatchers.IO) {
        quoteDao.insert(body, authorId) ?: run { throw IllegalStateException() }
    }

    override suspend fun updateQuote(
        quoteId: Int,
        body: String,
        authorId: Int?,
        tagIds: List<Int>?
    ) = withContext(Dispatchers.IO) {
        val quote = quoteDao.findById(quoteId) ?: throw IllegalArgumentException("Quote does not exists")
        val tagsEqual = tagIds == quote.tags
        if (!tagsEqual) tagIds?.forEach { tagDao.removeQuoteFromTag(quoteId, it) }
        quoteDao.update(quoteId, body)?.also { quote ->
            if (!tagsEqual) tagIds?.forEach { tagDao.addQuoteToTag(quote.id, it) }
        } ?: run { throw IllegalStateException() }
    }

    override suspend fun removeQuote(quoteId: Int) = withContext(Dispatchers.IO) {
        quoteDao.removeQuote(quoteId) > 0
    }

    override suspend fun getComments(quoteId: Int): List<Comment> = withContext(Dispatchers.IO) {
        commentDao.getComments(quoteId)
    }

    override suspend fun addComment(commentBody: String, quoteId: Int, userId: Int) = withContext(Dispatchers.IO) {
        quoteDao.findById(quoteId) ?: return@withContext false
        commentDao.insert(commentBody, quoteId, userId)
        return@withContext true
    }

    override suspend fun deleteComment(commentId: Int, userId: Int): Boolean = withContext(Dispatchers.IO) {
        val userIsRequesting = commentDao.findComment(userId)?.user == userId
        if (userIsRequesting) commentDao.remove(commentId) == 1 else throw IllegalArgumentException("User does not own it")
    }
}
