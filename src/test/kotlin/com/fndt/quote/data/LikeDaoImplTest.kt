package com.fndt.quote.data

import com.fndt.quote.data.util.populateDb
import com.fndt.quote.domain.dao.LikeDao
import com.fndt.quote.domain.dto.Like
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class LikeDaoImplTest {
    private val likeDao: LikeDao = LikeDaoImpl(DatabaseProvider)

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
    fun `Like works`() {
        // GIVEN
        val expected = Like(1, 1)

        // WHEN
        val actual = likeDao.like(expected)
        val actualFound = likeDao.find(expected)

        // THEN
        assertTrue { expected == actual && expected == actualFound }
    }

    @Test
    fun `Unlike works`() {
        // GIVEN
        val like = Like(1, 1)

        // WHEN
        likeDao.like(like)
        likeDao.unlike(like)
        val actual = likeDao.find(like)

        // THEN
        assertEquals(null, actual)
    }
}
