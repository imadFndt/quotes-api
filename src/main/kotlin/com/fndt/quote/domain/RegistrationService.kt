package com.fndt.quote.domain

interface RegistrationService {
    suspend fun registerUser(login: String, password: String): Boolean
}
