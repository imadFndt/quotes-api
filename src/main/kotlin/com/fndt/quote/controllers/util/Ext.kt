package com.fndt.quote.controllers.util

import com.fndt.quote.controllers.UserRolePrincipal
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
    val principal = principal<UserRolePrincipal>()
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

fun <T : RegularUserService> Route.getExt(
    path: String? = null,
    holder: ServiceHolder,
    block: suspend ApplicationCall.(T) -> Unit
): Route {
    return httpMethodsExt(path, holder, Route::get, Route::get, block)
}

fun <T : RegularUserService> Route.postExt(
    path: String? = null,
    holder: ServiceHolder,
    block: suspend ApplicationCall.(T) -> Unit
): Route {
    return httpMethodsExt(path, holder, Route::post, Route::post, block)
}

fun <T : RegularUserService> Route.deleteExt(
    path: String? = null,
    holder: ServiceHolder,
    block: suspend ApplicationCall.(T) -> Unit
): Route {
    return httpMethodsExt(path, holder, Route::delete, Route::delete, block)
}

fun <T : RegularUserService> Route.patchExt(
    path: String? = null,
    holder: ServiceHolder,
    block: suspend ApplicationCall.(T) -> Unit
): Route {
    return httpMethodsExt(path, holder, Route::patch, Route::patch, block)
}

private inline fun <T : RegularUserService> Route.httpMethodsExt(
    path: String? = null,
    holder: ServiceHolder,
    noinline httpMethod: (Route.(PipelineInterceptor<Unit, ApplicationCall>) -> Route),
    noinline httpMethodWithPath: (Route.(String, PipelineInterceptor<Unit, ApplicationCall>) -> Route),
    crossinline block: suspend ApplicationCall.(T) -> Unit
): Route {
    return path?.let {
        httpMethodWithPath(it) {
            val service = call.getServiceOrRespondFail<T>(holder) ?: return@httpMethodWithPath
            call.block(service)
        }
    } ?: run {
        httpMethod {
            val service = call.getServiceOrRespondFail<T>(holder) ?: return@httpMethod
            call.block(service)
        }
    }
}

/*
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"body":"Тело цитаты","date":"30", "author": {"id":3,"name":"Копатыч"}}' \
  http://0.0.0.0:8080/quotes
 */
