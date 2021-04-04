package com.fndt.quote

import com.fndt.quote.di.Modules
import com.fndt.quote.domain.manager.UrlSchemeProvider
import com.fndt.quote.rest.controllers.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.http.content.*
import io.ktor.routing.*
import io.ktor.serialization.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.slf4j.event.Level
import java.io.File

fun Application.module() {
    val host = environment.config.propertyOrNull("ktor.deployment.myUrlProperty")?.getString() ?: "0.0.0.0"
    val port = environment.config.propertyOrNull("ktor.deployment.port")?.getString() ?: "8080"

    val imagesPath = environment.config.propertyOrNull("ktor.deployment.imagesFolder")?.getString() ?: "images"
    val filePath = environment.config.propertyOrNull("ktor.deployment.filePath")?.getString() ?: "webapp"

    UrlSchemeProvider.initScheme("$host:$port")
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                encodeDefaults = true
            }
        )
    }
    install(Koin) {
        modules(
            Modules.dbModule,
            Modules.managerModule,
            Modules.imagesModule("$filePath/$imagesPath"),
            Modules.useCaseManagerModule,
            Modules.controllersModule,
            Modules.userControllerModule("./$filePath")
        )
    }

    val registrationController by inject<UserController>()
    val quotesController by inject<QuotesController>()
    val commentsController by inject<CommentsController>()
    val authController by inject<AuthController>()
    val selectionsController by inject<SelectionsController>()
    val moderatorController by inject<ModeratorController>()
    val adminController by inject<AdminController>()

    install(CallLogging) {
        level = Level.INFO
    }

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
        routeImages(filePath, imagesPath)
    }
}

fun Routing.routeImages(mainFolder: String, subfolder: String) {
    static("images") {
        staticRootFolder = File("./$mainFolder")
        files(subfolder)
    }
}
