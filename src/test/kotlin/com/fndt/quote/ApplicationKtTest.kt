package com.fndt.quote

import com.fndt.quote.controllers.QUOTES_ENDPOINT
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ApplicationKtTest

@InternalAPI
class ApplicationTest {
    @Test
    fun testRequests() = withTestApplication(Application::module) {
        with(
            handleRequest(HttpMethod.Get, QUOTES_ENDPOINT) {
                authHeader("a", "a")
            }
        ) {
            assertEquals(HttpStatusCode.OK, response.status())
        }
    }
}

@InternalAPI
fun TestApplicationRequest.authHeader(login: String, password: String) {
    addHeader("Authorization", "Basic ${"$login:$password".encodeBase64()}")
}
