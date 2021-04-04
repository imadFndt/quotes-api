package com.fndt.quote.domain.usecases.quotes

import com.fndt.quote.domain.PermissionException
import com.fndt.quote.domain.UseCaseTestInit
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.getDummyQuote
import com.fndt.quote.domain.getDummyUser
import com.fndt.quote.domain.repository.AuthorRepository
import com.fndt.quote.domain.repository.QuoteRepository
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AddQuoteUseCaseTest : UseCaseTestInit() {

    @MockK(relaxed = true)
    lateinit var quoteRepository: QuoteRepository

    @MockK(relaxed = true)
    lateinit var authorRepository: AuthorRepository

    private val dummyBody: String = "a"
    private val dummyAuthor: String = "a"

    private lateinit var useCase: AddQuoteUseCase

    @Test
    fun `add quote`() = runBlocking {
        val requestUser = getDummyUser(AuthRole.REGULAR)
        coEvery { quoteRepository.findById(any()) } returns getDummyQuote(dummyBody, AuthRole.REGULAR)
        useCase = AddQuoteUseCase(
            dummyBody, dummyAuthor, quoteRepository, authorRepository, requestUser, permissionManager, requestManager
        )
        useCase.run()
        verify(exactly = 1) { quoteRepository.add(any()) }
    }

    @Test
    fun `add quote failure`() {
        val requestUser = getDummyUser(AuthRole.REGULAR)
        coEvery { authorRepository.findByName(any()) } returns null
        coEvery { authorRepository.findById(any()) } returns null
        useCase = AddQuoteUseCase(
            dummyBody, dummyAuthor, quoteRepository, authorRepository, requestUser, permissionManager, requestManager
        )
        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }

    @Test
    fun `read only user add fail`() {
        val requestUser = getDummyUser(AuthRole.REGULAR).copy(blockedUntil = System.currentTimeMillis() + 20000)
        useCase = AddQuoteUseCase(
            dummyBody, dummyAuthor, quoteRepository, authorRepository, requestUser, permissionManager, requestManager
        )

        assertThrows<PermissionException> { runBlocking { useCase.run() } }
    }
}
