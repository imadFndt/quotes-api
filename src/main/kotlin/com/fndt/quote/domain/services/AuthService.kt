package com.fndt.quote.domain.services

import com.fndt.quote.domain.dto.User

interface AuthService {
    suspend fun checkCredentials(login: String, password: String): User?
}
