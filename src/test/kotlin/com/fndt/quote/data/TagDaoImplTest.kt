package com.fndt.quote.data

import com.fndt.quote.data.util.populateDb
import com.fndt.quote.domain.dao.TagDao
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class TagDaoImplTest {
    private val tagDao: TagDao = TagDaoImpl(DatabaseProvider)

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
    fun `add tag`() {
        val tag = tagDao.insert("Statham")
        val tagList = tagDao.getTags()

        assertTrue { tagList.contains(tag) }
    }

    @Test
    fun `delete tag`() {
        val tag = tagDao.insert("Statham")
        val tag2 = tagDao.insert("Hummingway")
        tag2 ?: throw NullPointerException()
        val tagList = tagDao.getTags().toMutableList()
        tagList.remove(tag2)

        tagDao.remove(tag2.id)
        val newTagList = tagDao.getTags()

        assertTrue { tagList == newTagList }
    }
}
