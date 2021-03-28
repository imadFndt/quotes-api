package com.fndt.quote

import com.fndt.quote.controllers.*
import com.fndt.quote.di.Modules
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import java.io.File

var URL_IMAGE_SCHEME: String? = null

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    URL_IMAGE_SCHEME =
        "http://${environment.config.propertyOrNull("ktor.deployment.host")?.getString() ?: "0.0.0.0"}/images/"
    install(ContentNegotiation) { json(Json { prettyPrint = true }) }
    install(Koin) {
        modules(
            Modules.dbModule,
            Modules.managerModule,
            Modules.useCaseManagerModule,
            Modules.controllersModule
        )
    }

    val registrationController by inject<UserController>()
    val quotesController by inject<QuotesController>()
    val commentsController by inject<CommentsController>()
    val authController by inject<AuthController>()
    val selectionsController by inject<SelectionsController>()
    val moderatorController by inject<ModeratorController>()
    val adminController by inject<AdminController>()

    install(Authentication) { authController.addBasicAuth(this) }
    routing {
        listOf(
            registrationController,
            quotesController,
            commentsController,
            selectionsController,
            moderatorController,
            adminController
        ).forEach { it.route(this) }
        routeImages()
    }
}

fun Routing.routeImages() {
    static("images") {
        staticRootFolder = File("./files")
        files("images")
    }
}
