package com.fndt.quote.domain.usecases.review

import com.fndt.quote.domain.UseCaseInitTest
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.getDummyQuote
import com.fndt.quote.domain.repository.QuoteRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

internal class ReviewUseCaseTest : UseCaseInitTest() {

    var quoteId: Int = 1

    @MockK(relaxed = true)
    lateinit var quoteRepository: QuoteRepository

    @MockK(relaxed = true)
    lateinit var requestingUser: User

    lateinit var useCase: ReviewUseCase<Quote>

    @Test
    fun approve() = runBlocking {
        every { quoteRepository.findById(quoteId) } returns getDummyQuote("", AuthRole.REGULAR)
        val decision = true
        useCase = ReviewUseCase(
            decision,
            SimpleReviewUseCaseAdapter.createQuoteReviewAdapter(quoteId, quoteRepository, permissionManager),
            requestingUser, requestManager
        )
        useCase.run()

        verify { quoteRepository.add(any()) }
    }

    @Test
    fun reject() = runBlocking {
        every { quoteRepository.findById(quoteId) } returns getDummyQuote("", AuthRole.REGULAR)
        val decision = false
        useCase = ReviewUseCase(
            decision,
            SimpleReviewUseCaseAdapter.createQuoteReviewAdapter(quoteId, quoteRepository, permissionManager),
            requestingUser, requestManager
        )
        useCase.run()

        verify { quoteRepository.remove(any()) }
    }

    @Test
    fun failed() {
        every { quoteRepository.findById(quoteId) } returns null
        val decision = false
        useCase = ReviewUseCase(
            decision,
            SimpleReviewUseCaseAdapter.createQuoteReviewAdapter(quoteId, quoteRepository, permissionManager),
            requestingUser, requestManager
        )
        org.junit.jupiter.api.assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }
}
