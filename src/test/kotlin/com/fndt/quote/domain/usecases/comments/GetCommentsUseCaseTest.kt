package com.fndt.quote.domain.usecases.comments

import com.fndt.quote.domain.UseCaseTestInit
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.getDummyQuote
import com.fndt.quote.domain.getDummyUser
import com.fndt.quote.domain.repository.CommentRepository
import com.fndt.quote.domain.repository.QuoteRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GetCommentsUseCaseTest : UseCaseTestInit() {

    @MockK(relaxed = true)
    lateinit var commentRepository: CommentRepository

    @MockK(relaxed = true)
    lateinit var quoteRepository: QuoteRepository

    lateinit var useCase: GetCommentsUseCase

    private val quoteId: Int = 1
    private val requestingUser: User = getDummyUser(AuthRole.REGULAR)

    @Test
    fun `get comment`() = runBlocking {
        every { quoteRepository.findById(any()) } returns getDummyQuote("", AuthRole.REGULAR)
        useCase = GetCommentsUseCase(
            quoteId, quoteRepository, commentRepository, requestingUser, permissionManager, requestManager
        )
        useCase.run()

        verify(exactly = 1) { commentRepository.get(any()) }
    }

    @Test
    fun `get comment failure`() {
        every { quoteRepository.findById(any()) } returns null
        useCase = GetCommentsUseCase(
            quoteId, quoteRepository, commentRepository, requestingUser, permissionManager, requestManager
        )
        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }
}
