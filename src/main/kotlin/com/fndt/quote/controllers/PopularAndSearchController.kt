package com.fndt.quote.controllers

import com.fndt.quote.controllers.factory.PopularAndSearchUseCaseFactory
import com.fndt.quote.controllers.util.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

class PopularAndSearchController(private val useCaseFactory: PopularAndSearchUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth("") {
        getExt(POPULARS_ENDPOINT) { principal -> respond(useCaseFactory.getPopularsUseCase(principal.user).run()) }
        getExt(SEARCH_ENDPOINT) { principal ->
            val query = parameters[QUERY_ARG] ?: run {
                respondText("Query not received", status = HttpStatusCode.NotAcceptable)
                return@getExt
            }
            respond(useCaseFactory.getSearchUseCase(query, principal.user).run())
        }
    }
}
