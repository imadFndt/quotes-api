package com.fndt.quote.domain

import com.fndt.quote.domain.dto.AuthRole
import com.fndt.quote.domain.dto.Quote
import com.fndt.quote.domain.dto.User
import io.mockk.coEvery
import io.mockk.slot
import kotlinx.coroutines.runBlocking

fun getDummyUser(role: AuthRole): User {
    return User(1, "name", "pass", role)
}

fun getDummyQuote(body: String, role: AuthRole): Quote {
    return Quote(
        body = body,
        createdAt = System.currentTimeMillis(),
        user = getDummyUser(role)
    )
}

fun RequestManager.mockRunBlocking() {
    val slot = slot<suspend () -> Unit>()
    coEvery { execute(capture(slot)) } answers { runBlocking { slot.captured.invoke() } }
}
