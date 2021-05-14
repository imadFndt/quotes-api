package com.fndt.quote.rest.factory

import com.fndt.quote.domain.dto.Like
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.*
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.add.AddUseCase
import com.fndt.quote.domain.usecases.base.UseCase
import com.fndt.quote.domain.usecases.get.GetQuoteOfTheDay
import com.fndt.quote.domain.usecases.get.QuoteSelectionUseCase
import com.fndt.quote.domain.usecases.review.ReviewUseCase
import com.fndt.quote.domain.usecases.review.SimpleReviewUseCaseAdapter
import com.fndt.quote.domain.usecases.users.LikeUseCase

class QuotesUseCaseFactory(
    private val repositoryProvider: RepositoryProvider,
    private val requestManager: RequestManager,
    private val permissionManager: UserPermissionManager,
    private val addAdapterProvider: AddAdapterProvider,
) {
    fun addQuotesUseCase(body: String, authorName: String, userRequesting: User): UseCase<Unit> {
        val adapter = addAdapterProvider.createAddQuoteAdapter(body, authorName)
        return AddUseCase(adapter, userRequesting, requestManager)
    }

    fun likeQuoteUseCase(like: Like, likeAction: Boolean, userRequesting: User): UseCase<Unit> {
        return LikeUseCase(like, likeAction, userRequesting, repositoryProvider, permissionManager, requestManager)
    }

    fun getReviewQuoteUseCase(quoteId: Int, decision: Boolean, requestingUser: User): UseCase<Unit> {
        val quoteRepository = repositoryProvider.getRepository<QuoteRepository>()
        val adapter = SimpleReviewUseCaseAdapter.createQuoteReviewAdapter(quoteId, quoteRepository, permissionManager)
        return ReviewUseCase(decision, adapter, requestingUser, requestManager)
    }

    fun getQuoteSelectionsUseCase(arguments: Map<String, Any?>, user: User): QuoteSelectionUseCase {
        return QuoteSelectionUseCase(arguments, repositoryProvider, user, permissionManager, requestManager)
    }

    fun getQuoteOfTheDay(user: User): UseCase<Quote> {
        return GetQuoteOfTheDay(repositoryProvider, user, permissionManager, requestManager)
    }
}
