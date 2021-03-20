package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.repository.*
import com.fndt.quote.domain.dto.Tag
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.services.ModeratorUserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal open class ModeratorUserServiceImpl(
    private val userRepository: UserRepository,
    commentRepository: CommentRepository,
    private val quoteRepository: QuoteRepository,
    likeRepository: LikeRepository,
    private val tagRepository: TagRepository,
    requestManager: RequestManager
) : RegularUserServiceImpl(commentRepository, quoteRepository, likeRepository, tagRepository, userRepository, requestManager), ModeratorUserService {
    override suspend fun setBanState(userId: Int, time: Int?): Boolean = withContext(Dispatchers.IO) {
        userRepository.findUser(userId) ?: throw IllegalArgumentException("User not found")
        userRepository.update(
            time = time?.let { System.currentTimeMillis() + it } ?: run { null },
            userId = userId
        ) != null
    }

    override suspend fun setQuoteVisibility(quoteId: Int, isPublic: Boolean): Boolean = withContext(Dispatchers.IO) {
        quoteRepository.findById(quoteId) ?: throw IllegalArgumentException("Quote not found")
        quoteRepository.update(quoteId, isPublic = isPublic) != null
    }

    override suspend fun addTagForModeration(tag: Tag): Boolean = withContext(Dispatchers.IO) {
        tagRepository.insert(tag.name) != null
    }

    override suspend fun getUserById(userId: Int): User = withContext(Dispatchers.IO) {
        userRepository.findUser(userId) ?: throw IllegalArgumentException("User not found")
    }
}
