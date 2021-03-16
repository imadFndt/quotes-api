package com.fndt.quote.controllers

import com.fndt.quote.controllers.util.getAndCheckIntParameter
import com.fndt.quote.controllers.util.getExt
import com.fndt.quote.controllers.util.routePathWithAuth
import com.fndt.quote.domain.ServiceHolder
import com.fndt.quote.domain.services.RegularUserService
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

const val AUTHORS_ENDPOINT = "/authors"

class AuthorsController(private val holder: ServiceHolder) : RoutingController {
    override fun route(routing: Routing) = routing.routePathWithAuth(AUTHORS_ENDPOINT) {
        getExt<RegularUserService>("{id}", holder) { service ->
            val id = getAndCheckIntParameter("id") ?: return@getExt
            respond(service.getQuotes(id))
        }
    }
}
