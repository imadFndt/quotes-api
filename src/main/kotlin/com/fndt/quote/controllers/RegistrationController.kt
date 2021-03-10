package com.fndt.quote.controllers

import com.fndt.quote.domain.RegistrationService
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

const val REGISTRATION_ENDPOINT = "/register"

class RegistrationController(private val service: RegistrationService) : RoutingController {
    override fun route(routing: Routing) = routing {
        route(REGISTRATION_ENDPOINT) {
            get {
                call.respondText { "a" }
            }
        }
    }
}
