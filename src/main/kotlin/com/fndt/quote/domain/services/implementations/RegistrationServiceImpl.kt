package com.fndt.quote.domain.services.implementations

import com.fndt.quote.domain.repository.UserRepository
import com.fndt.quote.domain.services.RegistrationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class RegistrationServiceImpl(private val userRepository: UserRepository) : RegistrationService {
    override suspend fun registerUser(login: String, password: String) = withContext(Dispatchers.IO) {
        userRepository.findUser(name = login)?.let { throw IllegalArgumentException("User already registered") }
        userRepository.insert(login, password) ?: throw IllegalStateException("Failed to register")
        Unit
    }
}
