package com.fndt.quote.controllers

import com.fndt.quote.controllers.dto.UserCredentials
import com.fndt.quote.controllers.factory.UsersUseCaseFactory
import com.fndt.quote.controllers.util.REGISTRATION_ENDPOINT
import com.fndt.quote.controllers.util.receiveCatching
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

class RegistrationController(private val useCaseManager: UsersUseCaseFactory) : RoutingController {
    override fun route(routing: Routing) = routing {
        suspend fun ApplicationCall.registerAndRespond() {
            val credentials = receiveCatching<UserCredentials>() ?: return
            val result = try {
                useCaseManager.registerUseCase(credentials.login, credentials.password).run()
                "Succeed" to HttpStatusCode.OK
            } catch (e: Exception) {
                "Failed" to HttpStatusCode.NotAcceptable
            }
            respondText(text = result.first, status = result.second)
        }
        route(REGISTRATION_ENDPOINT) { get { call.registerAndRespond() } }
    }
}
