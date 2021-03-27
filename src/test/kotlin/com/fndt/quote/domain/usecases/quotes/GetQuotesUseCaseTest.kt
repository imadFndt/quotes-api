package com.fndt.quote.domain.usecases.quotes

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.filter.QuoteFilterArguments
import com.fndt.quote.domain.filter.QuotesAccess
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

internal class GetQuotesUseCaseTest {
    @MockK(relaxed = true)
    lateinit var permissionManager: PermissionManager

    @MockK(relaxed = true)
    lateinit var quotesRepository: QuoteRepository

    @MockK(relaxed = true)
    lateinit var requestManager: RequestManager

    private lateinit var useCase: GetQuotesUseCase

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this)
        requestManager.mockRunBlocking<Unit>()
        coEvery { permissionManager.hasGetQuotesPermission(any()) } returns true
    }

    @Test
    fun `get quotes`() = runBlocking {
        val requestUser = getDummyUser(AuthRole.REGULAR)
        useCase = GetQuotesUseCase(null, quotesRepository, requestUser, permissionManager, requestManager)
        useCase.run()
        verify { quotesRepository.get(QuoteFilterArguments(user = null, access = QuotesAccess.PUBLIC)) }
    }

    @Test
    fun `get quotes moderator`() = runBlocking {
        val requestUser = getDummyUser(AuthRole.MODERATOR)
        useCase = GetQuotesUseCase(null, quotesRepository, requestUser, permissionManager, requestManager)
        useCase.run()
        verify { quotesRepository.get(QuoteFilterArguments(user = null, access = QuotesAccess.ALL)) }
    }
}
