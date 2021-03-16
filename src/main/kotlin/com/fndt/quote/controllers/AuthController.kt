package com.fndt.quote.controllers

import com.fndt.quote.domain.services.AuthService
import io.ktor.auth.*

class AuthController(private val service: AuthService) {
    fun addBasicAuth(authentication: Authentication.Configuration) = authentication.apply {
        basic {
            realm = "Ktor"
            validate { credentials ->
                val user = service.checkCredentials(credentials.name, credentials.password)
                user?.let { UserRolePrincipal(it) } ?: run { null }
            }
        }
    }
}
