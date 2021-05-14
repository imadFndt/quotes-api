package com.fndt.quote.requests

import com.fndt.quote.*
import com.fndt.quote.rest.dto.AddComment
import com.fndt.quote.rest.dto.out.OutComment
import io.ktor.http.*
import io.ktor.server.testing.*
import io.ktor.util.*

@InternalAPI
fun TestApplicationEngine.addComment(
    quoteId: Int,
    addComment: AddComment,
    credentials: String
): TestApplicationCall {
    return handleRequestWithAuth(HttpMethod.Post, "/quotes/$quoteId/comment", credentials) {
        addJsonHeader()
        setBody(serializer.toJsonString(addComment))
    }
}

@InternalAPI
fun TestApplicationEngine.getComments(
    quoteId: Int,
    credentials: String
): List<OutComment> {
    val call = handleRequestWithAuth(HttpMethod.Get, "/quotes/$quoteId/comment", credentials)
    return serializer.parseResponse(call.response)
}
