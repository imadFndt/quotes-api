package com.fndt.quote.domain.usecases.moderator

import com.fndt.quote.domain.UseCaseTestInit
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.getDummyQuote
import com.fndt.quote.domain.repository.QuoteRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ReviewQuoteUseCaseTest : UseCaseTestInit() {

    var quoteId: Int = 1

    @MockK(relaxed = true)
    lateinit var quoteRepository: QuoteRepository

    @MockK(relaxed = true)
    lateinit var requestingUser: User

    lateinit var useCase: ReviewQuoteUseCase

    @Test
    fun `approve quote`() = runBlocking {
        every { quoteRepository.findById(quoteId) } returns getDummyQuote("", AuthRole.REGULAR)
        val decision = true
        useCase = ReviewQuoteUseCase(
            quoteId, decision, quoteRepository, requestingUser, permissionManager, requestManager
        )
        useCase.run()

        verify { quoteRepository.add(any()) }
    }

    @Test
    fun `reject quote`() = runBlocking {
        every { quoteRepository.findById(quoteId) } returns getDummyQuote("", AuthRole.REGULAR)
        val decision = false
        useCase = ReviewQuoteUseCase(
            quoteId, decision, quoteRepository, requestingUser, permissionManager, requestManager
        )
        useCase.run()

        verify { quoteRepository.remove(any()) }
    }

    @Test
    fun `failed quote`() {
        every { quoteRepository.findById(quoteId) } returns null
        val decision = false
        useCase = ReviewQuoteUseCase(
            quoteId, decision, quoteRepository, requestingUser, permissionManager, requestManager
        )
        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }
}
