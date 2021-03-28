package com.fndt.quote.domain.usecases.users

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.UrlSchemeProvider
import com.fndt.quote.domain.manager.UserPermissionManager
import com.fndt.quote.domain.mockRunBlocking
import com.fndt.quote.domain.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AuthUseCaseTest {
    @MockK(relaxed = true)
    lateinit var permissionManager: UserPermissionManager

    @MockK(relaxed = true)
    lateinit var userRepository: UserRepository

    @MockK(relaxed = true)
    lateinit var requestManager: RequestManager

    private lateinit var useCase: AuthUseCase

    private val login = "a"
    private val password = "a"

    @BeforeEach
    fun init() {
        UrlSchemeProvider.initScheme("test/")
        MockKAnnotations.init(this)
        requestManager.mockRunBlocking<User>()
        coEvery { permissionManager.isAuthAllowed() } returns true
    }

    @Test
    fun `auth success`() = runBlocking<Unit> {
        every { userRepository.findUserByParams(name = any(), password = any()) } returns User(
            name = login,
            password = password
        )
        useCase = AuthUseCase(login, password, userRepository, permissionManager, requestManager)
        useCase.run()
    }

    @Test
    fun `auth ban`() {
        every { userRepository.findUserByParams(name = any(), password = any()) } returns User(
            1,
            login,
            password,
            blockedUntil = System.currentTimeMillis() + 100000
        )
        useCase = AuthUseCase(login, password, userRepository, permissionManager, requestManager)
        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }
}
