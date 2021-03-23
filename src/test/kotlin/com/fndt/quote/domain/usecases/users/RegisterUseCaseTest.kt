package com.fndt.quote.domain.usecases.users

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.mockRunBlocking
import com.fndt.quote.domain.repository.UserRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class RegisterUseCaseTest {

    @MockK(relaxed = true)
    lateinit var permissionManager: PermissionManager

    @MockK(relaxed = true)
    lateinit var userRepository: UserRepository

    @MockK(relaxed = true)
    lateinit var requestManager: RequestManager

    private lateinit var useCase: RegisterUseCase

    private val login = "a"
    private val password = "a"

    private val users = mutableListOf<User>()

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this)
        requestManager.mockRunBlocking<User>()
        coEvery { permissionManager.hasRegisterPermission(any()) } returns true

        val intSlot = slot<Int>()
        every { userRepository.findUserByParams(capture(intSlot)) } answers { users.find { it.id == intSlot.captured } }
        val nameSlot = slot<String>()
        every { userRepository.findUserByParams(name = capture(nameSlot)) } answers { users.find { it.name == nameSlot.captured } }
        val userSlot = slot<User>()
        every { userRepository.add(capture(userSlot)) } answers {
            val user = userSlot.captured.copy(id = users.size)
            users.add(user)
            user.id
        }
    }

    @Test
    fun `register success`() = runBlocking {
        useCase = RegisterUseCase(login, password, userRepository, permissionManager, requestManager)
        useCase.run()

        verify { userRepository.add(any()) }
    }

    @Test
    fun `already registered`() {
        users.add(User(name = login, password = password))
        useCase = RegisterUseCase(login, password, userRepository, permissionManager, requestManager)

        assertThrows<IllegalArgumentException> { runBlocking { useCase.run() } }
    }

    @Test
    fun `add failed`() {
        every { userRepository.add(any()) } returns 0

        useCase = RegisterUseCase(login, password, userRepository, permissionManager, requestManager)

        assertThrows<IllegalArgumentException> { runBlocking { useCase.run() } }
    }
}
