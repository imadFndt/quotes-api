package com.fndt.quote.rest.util

import com.fndt.quote.rest.dto.UserPrincipal
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*

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
    val principal = principal<UserPrincipal>() ?: throw IllegalStateException(MISSING_PRINCIPAL)
    block(principal)
}
