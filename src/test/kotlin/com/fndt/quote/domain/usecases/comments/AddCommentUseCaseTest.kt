package com.fndt.quote.domain.usecases.comments

import com.fndt.quote.domain.PermissionException
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

internal class AddCommentUseCaseTest : UseCaseTestInit() {

    @MockK(relaxed = true)
    lateinit var commentRepository: CommentRepository

    @MockK(relaxed = true)
    lateinit var quoteRepository: QuoteRepository

    lateinit var useCase: AddCommentUseCase

    private var body = "dummyBody"
    private var quoteId = 1
    private var user: User = getDummyUser(AuthRole.REGULAR)

    @Test
    fun `add success`() = runBlocking {
        every { quoteRepository.findById(any()) } returns getDummyQuote(body, AuthRole.ADMIN)
        useCase = AddCommentUseCase(
            body, quoteId, user, commentRepository, quoteRepository, permissionManager, requestManager
        )
        useCase.run()

        verify(exactly = 1) {
            commentRepository.add(any())
            commentRepository.findComment(any())
        }
    }

    @Test
    fun `quote not found`() {
        every { quoteRepository.findById(any()) } returns null

        useCase = AddCommentUseCase(
            body, quoteId, user, commentRepository, quoteRepository, permissionManager, requestManager
        )

        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }

    @Test
    fun `quote add fail`() {
        every { quoteRepository.findById(any()) } returns getDummyQuote(body, AuthRole.ADMIN)
        every { commentRepository.findComment(any()) } returns null

        useCase = AddCommentUseCase(
            body, quoteId, user, commentRepository, quoteRepository, permissionManager, requestManager
        )

        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }

    @Test
    fun `read only user add fail`() {

        useCase = AddCommentUseCase(
            body,
            quoteId,
            user.copy(blockedUntil = System.currentTimeMillis() + 10000),
            commentRepository,
            quoteRepository,
            permissionManager,
            requestManager
        )

        assertThrows<PermissionException> { runBlocking { useCase.run() } }
    }
}
