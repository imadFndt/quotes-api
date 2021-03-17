package com.fndt.quote.domain.services

interface RegistrationService {
    suspend fun registerUser(login: String, password: String)
}
