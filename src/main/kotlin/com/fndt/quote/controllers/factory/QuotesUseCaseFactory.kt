package com.fndt.quote.controllers.factory

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.repository.LikeRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.UseCase
import com.fndt.quote.domain.usecases.quotes.AddQuoteUseCase
import com.fndt.quote.domain.usecases.quotes.GetQuotesUseCase
import com.fndt.quote.domain.usecases.quotes.LikeUseCase

class QuotesUseCaseFactory(
    private val likeRepository: LikeRepository,
    private val userRepository: UserRepository,
    private val quoteRepository: QuoteRepository,
    private val requestManager: RequestManager,
    private val permissionManager: PermissionManager,
) {
    fun getQuotesUseCase(requestingUser: User, searchUser: User? = null): UseCase<List<Quote>> {
        return GetQuotesUseCase(searchUser, quoteRepository, requestingUser, permissionManager, requestManager)
    }

    fun addQuotesUseCase(body: String, userRequesting: User): UseCase<Quote> {
        return AddQuoteUseCase(body, quoteRepository, userRequesting, permissionManager, requestManager)
    }

    fun likeQuoteUseCase(like: Like, likeAction: Boolean, userRequesting: User): UseCase<Unit> {
        return LikeUseCase(
            like,
            likeAction,
            userRequesting,
            quoteRepository,
            userRepository,
            likeRepository,
            permissionManager,
            requestManager
        )
    }
}
