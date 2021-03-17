package com.fndt.quote.data

import com.fndt.quote.data.util.populateDb
import com.fndt.quote.domain.dao.UserDao
import com.fndt.quote.domain.dto.AuthRole
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class UserDaoImplTest {
    private val userDao: UserDao = UserDaoImpl(DatabaseProvider)

    @BeforeEach
    fun initDB() {
        Database.connect(
            "jdbc:h2:mem;DB_CLOSE_DELAY=-1",
            "org.h2.Driver",
            "root",
            ""
        ).apply {
            populateDb()
        }
    }

    @Test
    fun `Add user`() {
        // GIVEN
        val preInsertList = userDao.getUsers().toMutableList()
        val name = "b"
        val password = "b"

        // WHEN
        val result = userDao.insert(name, password)
        result?.let { preInsertList.add(it) }
        val postInsertList = userDao.getUsers()

        // THEN
        assertEquals(preInsertList, postInsertList)
    }

    @Test
    fun `Update user`() {
        val newUser = userDao.insert("b", "b")
        newUser ?: throw IllegalStateException()
        val updated = userDao.update(newUser.id, role = AuthRole.MODERATOR)
        updated ?: throw IllegalStateException()

        assertTrue {
            newUser.id == updated.id &&
                newUser.name == updated.name &&
                newUser.role != updated.role
        }
    }
}
