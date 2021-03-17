package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.UserCredentials
import com.fndt.quote.controllers.util.receiveCatching
import com.fndt.quote.domain.services.RegistrationService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

const val REGISTRATION_ENDPOINT = "/register"

class RegistrationController(private val service: RegistrationService) : RoutingController {
    override fun route(routing: Routing) = routing {
        suspend fun ApplicationCall.registerAndRespond() {
            val credentials = receiveCatching<UserCredentials>() ?: return
            service.registerUser(credentials.login, credentials.password)
            val result = try {
                service.registerUser(credentials.login, credentials.password)
                "Succeed" to HttpStatusCode.OK
            } catch (e: Exception) {
                "Failed" to HttpStatusCode.NotAcceptable
            }
            respondText(text = result.first, status = result.second)
        }
        route(REGISTRATION_ENDPOINT) { get { call.registerAndRespond() } }
    }
}
