package com.fndt.quote.controllers

import io.ktor.routing.*

interface RoutingController {
    fun route(routing: Routing)
}
