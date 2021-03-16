package com.fndt.quote.domain

import com.fndt.quote.domain.dao.*
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.services.RegularUserService
import com.fndt.quote.domain.services.implementations.AdminUserServiceImpl
import com.fndt.quote.domain.services.implementations.ModeratorUserServiceImpl
import com.fndt.quote.domain.services.implementations.RegularUserServiceImpl

class UserServiceFactory(
    private val userDao: UserDao,
    private val commentDao: CommentDao,
    private val likeDao: LikeDao,
    private val quoteDao: QuoteDao,
    private val authorDao: AuthorDao,
    private val tagDao: TagDao,
) {

    @Suppress("UNCHECKED_CAST")
    fun createService(role: AuthRole?): RegularUserService? {
        return when (role) {
            AuthRole.ADMIN -> AdminUserServiceImpl(userDao, commentDao, quoteDao, likeDao, tagDao, authorDao)
            AuthRole.MODERATOR -> ModeratorUserServiceImpl(userDao, commentDao, quoteDao, likeDao, tagDao, authorDao)
            AuthRole.REGULAR -> RegularUserServiceImpl(commentDao, quoteDao, likeDao, tagDao, authorDao)
            else -> null
        }
    }
}
