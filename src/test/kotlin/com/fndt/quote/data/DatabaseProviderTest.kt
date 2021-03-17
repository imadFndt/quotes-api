package com.fndt.quote.data

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DatabaseProviderTest {
    private val usersTable: DatabaseProvider.Users by DatabaseProvider

    @Test
    fun `get value delegate`() {
        assertEquals(DatabaseProvider.Users, usersTable)
    }
}
