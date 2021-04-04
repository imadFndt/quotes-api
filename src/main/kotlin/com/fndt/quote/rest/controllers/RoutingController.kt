package com.fndt.quote.rest.controllers

import io.ktor.routing.*

interface RoutingController {
    fun route(routing: Routing)
}
