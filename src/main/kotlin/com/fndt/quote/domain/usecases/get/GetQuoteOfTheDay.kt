package com.fndt.quote.domain.usecases.get

import com.fndt.quote.data.RandomQuoteRepository
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.filter.Access
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesOrder
import com.fndt.quote.domain.manager.RepositoryProvider
import com.fndt.quote.domain.manager.RequestManager
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.manager.getRepository
import com.fndt.quote.domain.repository.QuoteRepository
import com.fndt.quote.domain.usecases.base.RequestUseCase
import java.time.LocalDate

class GetQuoteOfTheDay(
    repositoryProvider: RepositoryProvider,
    override val requestingUser: User,
    private val permissionManager: UserPermissionManager,
    requestManager: RequestManager
) : RequestUseCase<Quote>(requestManager) {
    private val randomQuoteRepository = repositoryProvider.getRepository<RandomQuoteRepository>()
    private val quoteRepository = repositoryProvider.getRepository<QuoteRepository>()

    override fun validate(user: User?) = permissionManager.isAuthorized(user)

    override suspend fun makeRequest(): Quote {
        val date = LocalDate.now()
        return randomQuoteRepository.getRandomQuote(requestingUser, date)?.let {
            quoteRepository.findById(it) ?: throw IllegalStateException()
        } ?: generateNew(requestingUser, date)
    }

    private fun generateNew(user: User, date: LocalDate): Quote = quoteRepository
        .get(QuoteFilterArguments(requestingUser = user, order = QuotesOrder.RANDOM, quoteAccess = Access.PUBLIC))
        .first().also { quote ->
            randomQuoteRepository.addRandomQuote(user, date, quote)
        }
}
