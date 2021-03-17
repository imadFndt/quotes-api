package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.dao.UserDao
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.services.RegistrationService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

internal class RegistrationServiceImplTest {
    @MockK(relaxed = true)
    lateinit var userDao: UserDao

    lateinit var service: RegistrationService

    private val login = "a"
    private val password = "a"
    private val users = mutableListOf<User>()

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        val loginSlot = slot<String>()
        val passwordSlot = slot<String>()
        every { userDao.findUser(name = capture(loginSlot)) } answers {
            users.find { it.name == loginSlot.captured }
        }
        every { userDao.insert(capture(loginSlot), capture(passwordSlot)) } answers {
            val user = User(users.size + 1, loginSlot.captured, passwordSlot.captured)
            users.add(user)
            users.find { it.id == user.id }
        }
        service = RegistrationServiceImpl(userDao)
    }

    @Test
    fun `user registration`() = runBlocking {
        val usersSize = users.size

        service.registerUser(login, password)

        assertTrue { users.size == usersSize + 1 }
        verify(exactly = 1) { userDao.insert(login, password) }
    }

    @Test
    fun `existing user registration`() = runBlocking {
        assertThrows<IllegalArgumentException> {
            runBlocking {
                service.registerUser(login, password)
                service.registerUser(login, password)
            }
        }
        verify(exactly = 1) { userDao.insert(login, password) }
    }
}
