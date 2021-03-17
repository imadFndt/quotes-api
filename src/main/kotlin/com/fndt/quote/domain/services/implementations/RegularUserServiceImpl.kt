package com.fndt.quote.domain.services.implementations

import com.fndt.quote.data.QuoteDaoImpl
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
    private val authorDao: AuthorDao
) : RegularUserService {

    override suspend fun getQuotes(id: Int?): List<Quote> = quoteDao.getQuotes(id)

    override suspend fun setQuoteLike(like: Like, likeAction: Boolean): Boolean = withContext(Dispatchers.IO) {
        val likeExists = likeDao.find(like) != null
        when {
            likeAction && !likeExists -> likeDao.like(like) != null
            likeAction && likeExists -> likeDao.unlike(like) == 1
            else -> return@withContext false
        }
    }

    override suspend fun upsertQuote(
        body: String,
        authorId: Int,
        tagId: List<Int>,
        quoteId: Int?
    ): Boolean = withContext(Dispatchers.IO) {
        val comment = commentDao.findComment(authorId)
        val commentExists = comment != null
        val userIsRequesting = comment?.user == authorId
        authorDao.findById(authorId) ?: throw IllegalArgumentException("Author does not exists")
        when {
            commentExists && userIsRequesting -> {
                (quoteDao as QuoteDaoImpl).update(quoteId!!, body, authorId)?.let { quote ->
                    tagId.forEach { tagId ->
                        if (quote.tags.find { it.id == tagId } == null) tagDao.addQuoteToTag(quote.id, tagId)
                    }
                } ?: run { throw IllegalStateException() }
                true
            }
            !commentExists -> {
                (quoteDao as QuoteDaoImpl).insert(body, authorId)?.let { quote ->
                    tagId.forEach { tagDao.addQuoteToTag(quote.id, it) }
                    true
                } ?: run { throw IllegalStateException() }
            }
            else -> {
                throw IllegalArgumentException("User does not own it")
            }
        }
    }

    override suspend fun removeQuote(quoteId: Int) = withContext(Dispatchers.IO) {
        quoteDao.removeQuote(quoteId) > 0
    }

    override suspend fun getComments(quoteId: Int): List<Comment> = withContext(Dispatchers.IO) {
        commentDao.getComments(quoteId)
    }

    override suspend fun addComment(commentBody: String, quoteId: Int, userId: Int): Boolean =
        withContext(Dispatchers.IO) {
            if (quoteDao.findById(quoteId) != null) return@withContext false
            commentDao.insert(commentBody, quoteId, userId)
            return@withContext true
        }

    override suspend fun deleteComment(commentId: Int, userId: Int): Boolean = withContext(Dispatchers.IO) {
        val userIsRequesting = commentDao.findComment(userId)?.user == userId
        if (userIsRequesting) commentDao.deleteComment(commentId) == 1 else throw IllegalArgumentException("User does not own it")
    }
}
