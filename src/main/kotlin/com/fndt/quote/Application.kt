package com.fndt.quote

import com.fndt.quote.controllers.AuthController
import com.fndt.quote.controllers.QuotesController
import com.fndt.quote.controllers.RegistrationController
import com.fndt.quote.di.Modules
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) { json(json = Json { prettyPrint = true }) }
    install(Koin) { modules(Modules.dbModule, Modules.serviceModule, Modules.controllersModule) }

    val registrationController by inject<RegistrationController>()
    val quotesController by inject<QuotesController>()
    val authController by inject<AuthController>()

    install(Authentication) { authController.addBasicAuth(this) }
    routing {
        listOf(registrationController, quotesController).forEach { it.route(this) }
    }
}
