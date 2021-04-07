package com.fndt.quote.domain.usecases.get

import com.fndt.quote.domain.UseCaseInitTest
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.getDummyQuote
import com.fndt.quote.domain.getDummyUser
import com.fndt.quote.domain.manager.RepositoryProvider
import com.fndt.quote.domain.repository.CommentRepository
import com.fndt.quote.domain.repository.QuoteRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GetCommentsUseCaseTest : UseCaseInitTest() {

    @MockK(relaxed = true)
    lateinit var repositoryProvider: RepositoryProvider

    @MockK(relaxed = true)
    lateinit var commentRepository: CommentRepository

    @MockK(relaxed = true)
    lateinit var quoteRepository: QuoteRepository

    lateinit var useCase: GetCommentsUseCase

    private val quoteId: Int = 1
    private val requestingUser: User = getDummyUser(AuthRole.REGULAR)

    @BeforeEach
    override fun init() {
        super.init()
        every { repositoryProvider.getRepository(CommentRepository::class) } returns commentRepository
        every { repositoryProvider.getRepository(QuoteRepository::class) } returns quoteRepository
    }

    @Test
    fun `get comment`() = runBlocking {
        every { quoteRepository.findById(any()) } returns getDummyQuote("", AuthRole.REGULAR)
        useCase = GetCommentsUseCase(
            quoteId, repositoryProvider, requestingUser, permissionManager, requestManager
        )
        useCase.run()

        verify(exactly = 1) { commentRepository.get(any()) }
    }

    @Test
    fun `get comment failure`() {
        every { quoteRepository.findById(any()) } returns null
        useCase = GetCommentsUseCase(
            quoteId, repositoryProvider, requestingUser, permissionManager, requestManager
        )
        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }
}
