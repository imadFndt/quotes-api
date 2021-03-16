package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.UserCredentials
import com.fndt.quote.controllers.util.receiveCatching
import com.fndt.quote.domain.services.RegistrationService
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*

const val REGISTRATION_ENDPOINT = "/register"

class RegistrationController(private val service: RegistrationService) : RoutingController {
    override fun route(routing: Routing) = routing {
        suspend fun ApplicationCall.registerAndRespond() {
            val credentials = receiveCatching<UserCredentials>() ?: return
            val registerSuccess = service.registerUser(credentials.login, credentials.password)
            respondText(
                text = "Registration ${if (registerSuccess) "succeed" else "failed"}",
                status = if (registerSuccess) io.ktor.http.HttpStatusCode.OK else io.ktor.http.HttpStatusCode.NotAcceptable
            )
        }
        route(REGISTRATION_ENDPOINT) { get { call.registerAndRespond() } }
    }
}
