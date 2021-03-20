package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.repository.*
import com.fndt.quote.domain.services.AdminUserService
import org.jetbrains.exposed.sql.transactions.transaction

internal class AdminUserServiceImpl(
    private val userRepository: UserRepository,
    commentRepository: CommentRepository,
    quoteRepository: QuoteRepository,
    likeRepository: LikeRepository,
    requestManager: RequestManager,
    private val tagRepository: TagRepository,
) : ModeratorUserServiceImpl(
        userRepository,
        commentRepository,
        quoteRepository,
        likeRepository,
        tagRepository,
        requestManager
    ),
    AdminUserService {
    override suspend fun setTagVisibility(tagId: Int, isPublic: Boolean): Boolean = transaction {
        tagRepository.update(tagId = tagId, isPublic = isPublic) != null
    }

    override suspend fun changeRole(userId: Int, newRole: AuthRole, oldRole: AuthRole): Boolean = transaction {
        userRepository.update(userId, role = newRole) != null
    }
}
