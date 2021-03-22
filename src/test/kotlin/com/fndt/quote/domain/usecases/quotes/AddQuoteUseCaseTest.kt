package com.fndt.quote.domain.usecases.quotes

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.getDummyQuote
import com.fndt.quote.domain.getDummyUser
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.mockRunBlocking
import com.fndt.quote.domain.repository.QuoteRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AddQuoteUseCaseTest {

    @MockK(relaxed = true)
    lateinit var permissionManager: PermissionManager

    @MockK(relaxed = true)
    lateinit var quoteRepository: QuoteRepository

    @MockK(relaxed = true)
    lateinit var requestManager: RequestManager

    private val dummyBody: String = "a"

    private lateinit var useCase: AddQuoteUseCase

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        requestManager.mockRunBlocking()
        coEvery { permissionManager.hasAddQuotePermission(any()) } returns true
    }

    @Test
    fun `add test`() = runBlocking {
        val requestUser = getDummyUser(AuthRole.REGULAR)
        coEvery { quoteRepository.findById(any()) } returns getDummyQuote(dummyBody, AuthRole.REGULAR)
        useCase = AddQuoteUseCase(dummyBody, quoteRepository, requestUser, permissionManager, requestManager)
        useCase.run()
        verify(exactly = 1) { quoteRepository.add(any()) }
        verify(exactly = 1) { quoteRepository.findById(any()) }
    }

    @Test
    fun `add test failure`() {
        coEvery { permissionManager.hasAddQuotePermission(any()) } returns true
        val requestUser = getDummyUser(AuthRole.REGULAR)
        coEvery { quoteRepository.findById(any()) } returns null
        useCase = AddQuoteUseCase(dummyBody, quoteRepository, requestUser, permissionManager, requestManager)
        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
        verify(exactly = 1) { quoteRepository.add(any()) }
        verify(exactly = 1) { quoteRepository.findById(any()) }
    }
}
