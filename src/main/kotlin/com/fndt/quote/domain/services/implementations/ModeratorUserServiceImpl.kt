package com.fndt.quote.domain.services.implementations

import com.fndt.quote.data.QuoteDaoImpl
import com.fndt.quote.domain.dao.*
import com.fndt.quote.domain.dto.Tag
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
    override suspend fun banUser(userId: Int, time: Int): Boolean = withContext(Dispatchers.IO) {
        userDao.update(time = System.currentTimeMillis() + time, userId = userId) != null
    }

    override suspend fun setQuoteVisibility(quoteId: Int, isPublic: Boolean): Boolean = withContext(Dispatchers.IO) {
        (quoteDao as QuoteDaoImpl).update(quoteId, isPublic = isPublic) != null
    }

    override suspend fun addTagForModeration(tag: Tag): Boolean = withContext(Dispatchers.IO) {
        tagDao.upsertTag(tag.name) != null
    }
}
