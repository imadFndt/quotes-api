package com.fndt.quote.rest.util

import com.fndt.quote.domain.PermissionException
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import kotlinx.coroutines.flow.*

fun <T> ApplicationCall.processRequest(block: suspend ApplicationCall.() -> T): Flow<T> = flow {
    emit(block())
}

fun <T> ApplicationCall.defaultCatch(): (suspend FlowCollector<T>.(Throwable) -> Unit) {
    return {
        val status = if (it is PermissionException) HttpStatusCode.Unauthorized else HttpStatusCode.BadRequest
        respondText("Throwable: ${it.message}", status = status)
    }
}

suspend fun <T> Flow<T>.collectSuccessResponse(call: ApplicationCall) {
    return collect {
        call.respond(SUCCESS)
    }
}

suspend fun <T> Flow<T>.respondPostDefault(call: ApplicationCall) {
    catch(call.defaultCatch())
        .collect { call.respond(SUCCESS) }
}
