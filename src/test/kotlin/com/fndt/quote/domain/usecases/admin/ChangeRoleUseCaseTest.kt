package com.fndt.quote.domain.usecases.admin

import com.fndt.quote.domain.UseCaseTestInit
import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.getDummyUser
import com.fndt.quote.domain.repository.UserRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ChangeRoleUseCaseTest : UseCaseTestInit() {

    @MockK(relaxed = true)
    lateinit var userRepository: UserRepository

    lateinit var useCase: ChangeRoleUseCase

    private val userId: Int = 1
    private val newRole: AuthRole = AuthRole.MODERATOR
    private val requestingUser: User = getDummyUser(AuthRole.ADMIN)

    @Test
    fun `change success`() = runBlocking {
        useCase = ChangeRoleUseCase(userId, newRole, userRepository, requestingUser, permissionManager, requestManager)
        useCase.run()
        verify { userRepository.add(any()) }
    }

    @Test
    fun `change failure`() {
        every { userRepository.findUserByParams(userId = any()) } returns null
        useCase = ChangeRoleUseCase(userId, newRole, userRepository, requestingUser, permissionManager, requestManager)
        assertThrows<IllegalStateException> { runBlocking { useCase.run() } }
    }
}
