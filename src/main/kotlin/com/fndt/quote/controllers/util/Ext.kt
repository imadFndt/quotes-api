package com.fndt.quote.controllers.util

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import kotlinx.serialization.SerializationException

// todo suspend nullable lambda
suspend inline fun <reified T : Any> ApplicationCall.receiveCatching(block: ApplicationCall.(T) -> Unit) {
    try {
        block(receive())
    } catch (e: SerializationException) {
        respondText(text = "Malformed json", status = HttpStatusCode.UnsupportedMediaType)
    }
}

/*
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"body":"Тело цитаты","date":"30", "author": {"id":3,"name":"Копатыч"}}' \
  http://0.0.0.0:8080/quotes
 */
