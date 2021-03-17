package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.dao.UserDao
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.services.AuthService
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotEquals

internal class AuthServiceImplTest {
    @MockK(relaxed = true)
    lateinit var userDao: UserDao

    lateinit var service: AuthService

    private val users = mutableListOf(User(1, "a", "a"))

    @BeforeEach
    fun init() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        val loginSlot = slot<String>()
        val passwordSlot = slot<String>()
        every { userDao.findUser(name = capture(loginSlot), password = capture(passwordSlot)) } answers {
            users.find { it.name == loginSlot.captured && it.hashedPassword == passwordSlot.captured }
        }
        service = AuthServiceImpl(userDao)
    }

    @Test
    fun `Credentials success`() = runBlocking {
        val user = service.checkCredentials("a", "a")

        assertNotEquals(null, user)
        verify(exactly = 1) { userDao.findUser(name = "a", password = "a") }
    }
}
