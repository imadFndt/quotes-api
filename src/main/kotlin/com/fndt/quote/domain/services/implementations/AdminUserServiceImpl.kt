package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.dao.*
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.services.AdminUserService
import org.jetbrains.exposed.sql.transactions.transaction

internal class AdminUserServiceImpl(
    private val userDao: UserDao,
    commentDao: CommentDao,
    quoteDao: QuoteDao,
    likeDao: LikeDao,
    private val tagDao: TagDao,
) : ModeratorUserServiceImpl(userDao, commentDao, quoteDao, likeDao, tagDao), AdminUserService {
    override suspend fun setTagVisibility(tagId: Int, isPublic: Boolean): Boolean = transaction {
        tagDao.update(tagId = tagId, isPublic = isPublic) != null
    }

    override suspend fun changeRole(userId: Int, newRole: AuthRole, oldRole: AuthRole): Boolean = transaction {
        userDao.update(userId, role = newRole) != null
    }
}
