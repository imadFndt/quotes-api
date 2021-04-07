package com.fndt.quote.requests

import com.fndt.quote.domain.addJsonHeader
import com.fndt.quote.domain.handleRequestWithAuth
import com.fndt.quote.domain.parseResponse
import com.fndt.quote.domain.serializer
import com.fndt.quote.rest.dto.UserCredentials
import com.fndt.quote.rest.dto.out.OutUser
import com.fndt.quote.rest.util.REGISTRATION_ENDPOINT
import com.fndt.quote.rest.util.ROLE_ENDPOINT
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*
import kotlinx.serialization.json.encodeToJsonElement

@InternalAPI
fun TestApplicationEngine.getUserByCredentials(credentials: String): OutUser {
    val call = handleRequestWithAuth(HttpMethod.Get, ROLE_ENDPOINT, credentials)
    return serializer.parseResponse(call.response)
}

@InternalAPI
fun TestApplicationEngine.register(login: String, password: String): TestApplicationCall {
    return handleRequest(HttpMethod.Post, REGISTRATION_ENDPOINT) {
        addJsonHeader()
        setBody(serializer.encodeToJsonElement(UserCredentials(login, password)).toString())
    }
}

@InternalAPI
fun TestApplicationEngine.banUser(
    userId: Int,
    credentials: String
): TestApplicationCall {
    return handleRequestWithAuth(HttpMethod.Post, "/ban/$userId", credentials)
}
