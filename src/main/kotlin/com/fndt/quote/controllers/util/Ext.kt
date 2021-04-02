package com.fndt.quote.controllers.util

import com.fndt.quote.controllers.dto.UserPrincipal
import com.fndt.quote.domain.PermissionException
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.SerializationException

// todo suspend nullable lambda
suspend inline fun <reified T : Any> ApplicationCall.receiveCatching(): T? {
    return try {
        receive()
    } catch (e: SerializationException) {
        null
    }
}

fun ApplicationCall.getAndCheckIntParameter(parameterName: String): Int? {
    return try {
        parameters[parameterName]?.toInt()
    } catch (e: NumberFormatException) {
        null
    }
}

fun Route.routePathWithAuth(basePath: String, routingBlock: Route.() -> Unit) {
    route(basePath) { authenticate { routingBlock() } }
}

fun Route.getExt(
    path: String? = null,
    block: suspend ApplicationCall.(UserPrincipal) -> Unit
): Route {
    return httpMethodsPrincipalExt(path, Route::get, Route::get, block)
}

fun Route.postExt(
    path: String? = null,
    block: suspend ApplicationCall.(UserPrincipal) -> Unit
): Route {
    return httpMethodsPrincipalExt(path, Route::post, Route::post, block)
}

fun Route.deleteExt(
    path: String? = null,
    block: suspend ApplicationCall.(UserPrincipal) -> Unit
): Route {
    return httpMethodsPrincipalExt(path, Route::delete, Route::delete, block)
}

fun Route.patchExt(
    path: String? = null,
    block: suspend ApplicationCall.(UserPrincipal) -> Unit
): Route {
    return httpMethodsPrincipalExt(path, Route::patch, Route::patch, block)
}

private fun Route.httpMethodsPrincipalExt(
    path: String? = null,
    httpMethod: Route.(PipelineInterceptor<Unit, ApplicationCall>) -> Route,
    httpMethodWithPath: Route.(String, PipelineInterceptor<Unit, ApplicationCall>) -> Route,
    block: suspend ApplicationCall.(UserPrincipal) -> Unit
): Route {
    return path?.let { httpMethodWithPath(it) { call.initBlockWithPrincipal(block) } }
        ?: run { httpMethod { call.initBlockWithPrincipal(block) } }
}

private suspend fun ApplicationCall.initBlockWithPrincipal(block: suspend ApplicationCall.(UserPrincipal) -> Unit) {
    try {
        val principal = principal<UserPrincipal>() ?: throw IllegalStateException(MISSING_PRINCIPAL)
        block(principal)
    } catch (e: PermissionException) {
        respondText(e.message.toString(), status = HttpStatusCode.Unauthorized)
    } catch (e: IllegalStateException) {
        respondText(e.message.toString(), status = HttpStatusCode.BadRequest)
    } catch (e: IllegalArgumentException) {
        respondText(e.message.toString(), status = HttpStatusCode.NotAcceptable)
    }
}

suspend fun ApplicationCall.tryResult(block: suspend () -> Unit): Pair<String, HttpStatusCode> {
    return try {
        block()
        SUCCESS to HttpStatusCode.OK
    } catch (e: Exception) {
        application.environment.log.info("Caught exception ${e.message}")
        FAILURE to HttpStatusCode.NotAcceptable
    }
}
