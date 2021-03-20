package com.fndt.quote.controllers.util

import com.fndt.quote.controllers.UserPrincipal
import com.fndt.quote.domain.ServiceHolder
import com.fndt.quote.domain.services.RegularUserService
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
        respondText(text = "Malformed json", status = HttpStatusCode.UnsupportedMediaType)
        null
    }
}

suspend fun ApplicationCall.getAndCheckIntParameter(parameterName: String): Int? {
    return try {
        parameters[parameterName]?.toInt() ?: run {
            respondText("Missing or malformed id", status = HttpStatusCode.BadRequest)
            null
        }
    } catch (e: NumberFormatException) {
        respondText("Malformed id", status = HttpStatusCode.BadRequest)
        null
    }
}

suspend fun <T : RegularUserService> ApplicationCall.getServiceOrRespondFail(serviceHolder: ServiceHolder): T? {
    val principal = principal<UserPrincipal>()
    return serviceHolder.getUserService(principal?.user?.role) ?: run {
        respondText("Request failed", status = HttpStatusCode.BadRequest)
        null
    }
}

fun Route.routePathWithAuth(basePath: String, routingBlock: Route.() -> Route) {
    route(basePath) {
        authenticate {
            routingBlock()
        }
    }
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
    return path?.let {
        httpMethodWithPath(it) {
            call.getPrincipalAndCallBlock(block)
        }
    } ?: run {
        httpMethod {
            call.getPrincipalAndCallBlock(block)
        }
    }
}

private suspend fun ApplicationCall.getPrincipalAndCallBlock(block: suspend ApplicationCall.(UserPrincipal) -> Unit) {
    val principal = principal<UserPrincipal>() ?: throw IllegalStateException("Principal not found")
    block(principal)
}

/*
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"body":"Тело цитаты","date":"30", "author": {"id":3,"name":"Копатыч"}}' \
  http://0.0.0.0:8080/quotes
 */
