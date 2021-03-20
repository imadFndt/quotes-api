package com.fndt.quote.domain.manager.implementations

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.manager.QuotesUseCaseManager
import com.fndt.quote.domain.repository.LikeRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.UseCase
import com.fndt.quote.domain.usecases.quotes.AddQuoteUseCase
import com.fndt.quote.domain.usecases.quotes.GetQuotesUseCase
import com.fndt.quote.domain.usecases.quotes.LikeUseCase

class QuotesUseCaseManagerImpl(
    private val likeRepository: LikeRepository,
    private val userRepository: UserRepository,
    private val quoteRepository: QuoteRepository,
    private val requestManager: RequestManager,
    private val permissionManager: PermissionManager,
) : QuotesUseCaseManager {
    override fun getQuotesUseCase(
        userId: Int?,
        isPublic: Boolean?,
        orderPopulars: Boolean,
        tagId: Int?,
        userRequesting: User?,
    ): UseCase<List<Quote>> {
        return GetQuotesUseCase(
            userId,
            isPublic,
            orderPopulars,
            tagId,
            quoteRepository,
            userRequesting,
            permissionManager,
            requestManager
        )
    }

    override fun addQuotesUseCase(body: String, userId: Int, userRequesting: User?): UseCase<Quote> {
        if (!permissionManager.hasGetQuotesPermission(userRequesting)) throw PermissionException("User has no permission")
        return AddQuoteUseCase(body, userId, quoteRepository, userRequesting, permissionManager, requestManager)
    }

    override fun likeQuoteUseCase(like: Like, likeAction: Boolean, userRequesting: User?): UseCase<Like> {
        return LikeUseCase(
            like,
            likeAction,
            quoteRepository,
            userRepository,
            likeRepository,
            userRequesting,
            permissionManager,
            requestManager
        )
    }
}
