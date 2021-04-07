package com.fndt.quote.domain.usecases.users

import com.fndt.quote.domain.PermissionException
import com.fndt.quote.domain.UseCaseInitTest
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.getDummyUser
import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.usecases.ban.BanReadOnlyUserUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class BanReadOnlyUserUseCaseTest : UseCaseInitTest() {

    @MockK(relaxed = true)
    lateinit var userRepository: UserRepository

    @MockK(relaxed = true)
    lateinit var requestingUser: User

    lateinit var useCase: BanReadOnlyUserUseCase

    private val userId: Int = 1

    @Test
    fun `ban success`() = runBlocking {
        every { userRepository.findUserByParams(userId) } returns getDummyUser(AuthRole.REGULAR)
        useCase = BanReadOnlyUserUseCase(userId, userRepository, requestingUser, permissionManager, requestManager)
        useCase.run()
        verify { userRepository.add(any()) }
    }

    @Test
    fun `ban admin failure`() {
        every { userRepository.findUserByParams(userId) } returns getDummyUser(AuthRole.ADMIN)
        useCase = BanReadOnlyUserUseCase(userId, userRepository, requestingUser, permissionManager, requestManager)
        assertThrows<PermissionException> { runBlocking { useCase.run() } }
    }

    @Test
    fun `ban user not found failure`() {
        every { userRepository.findUserByParams(userId) } returns null
        useCase = BanReadOnlyUserUseCase(userId, userRepository, requestingUser, permissionManager, requestManager)
        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }
}
