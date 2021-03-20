package com.fndt.quote.domain.services

import com.fndt.quote.domain.dto.User

interface AuthService {
    suspend fun authenticate(login: String, password: String): User?
}
