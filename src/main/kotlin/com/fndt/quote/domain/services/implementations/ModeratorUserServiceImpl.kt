package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.dao.*
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.services.ModeratorUserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal open class ModeratorUserServiceImpl(
    private val userDao: UserDao,
    commentDao: CommentDao,
    private val quoteDao: QuoteDao,
    likeDao: LikeDao,
    private val tagDao: TagDao,
) : RegularUserServiceImpl(commentDao, quoteDao, likeDao, tagDao, userDao), ModeratorUserService {
    override suspend fun setBanState(userId: Int, time: Int?): Boolean = withContext(Dispatchers.IO) {
        userDao.findUser(userId) ?: throw IllegalArgumentException("User not found")
        userDao.update(
            time = time?.let { System.currentTimeMillis() + it } ?: run { null },
            userId = userId
        ) != null
    }

    override suspend fun setQuoteVisibility(quoteId: Int, isPublic: Boolean): Boolean = withContext(Dispatchers.IO) {
        quoteDao.findById(quoteId) ?: throw IllegalArgumentException("Quote not found")
        quoteDao.update(quoteId, isPublic = isPublic) != null
    }

    override suspend fun addTagForModeration(tag: Tag): Boolean = withContext(Dispatchers.IO) {
        tagDao.insert(tag.name) != null
    }

    override suspend fun getUserById(userId: Int): User = withContext(Dispatchers.IO) {
        userDao.findUser(userId) ?: throw IllegalArgumentException("User not found")
    }
}
