package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.UserCredentials
import com.fndt.quote.domain.RegistrationService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

const val REGISTRATION_ENDPOINT = "/register"

class RegistrationController(private val service: RegistrationService) : RoutingController {
    override fun route(routing: Routing) = routing {
        route(REGISTRATION_ENDPOINT) {
            get {
                call.receiveCatching<UserCredentials> { credentials ->
                    val registerSuccess = service.registerUser(credentials.login, credentials.password)
                    respondText(
                        text = "Registration ${if (registerSuccess) "succeed" else "failed"}",
                        status = if (registerSuccess) HttpStatusCode.OK else HttpStatusCode.NotAcceptable
                    )
                }
            }
        }
    }
}
