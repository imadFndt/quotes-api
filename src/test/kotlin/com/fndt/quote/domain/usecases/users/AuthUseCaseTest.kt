package com.fndt.quote.domain.usecases.users

import com.fndt.quote.domain.UseCaseTestInit
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.repository.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class AuthUseCaseTest : UseCaseTestInit() {

    @MockK(relaxed = true)
    lateinit var userRepository: UserRepository

    private lateinit var useCase: AuthUseCase

    private val login = "a"
    private val password = "a"

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
    fun `auth failure`() {
        every { userRepository.findUserByParams(name = any(), password = any()) } returns null
        useCase = AuthUseCase(login, password, userRepository, permissionManager, requestManager)
        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }
}
