package com.fndt.quote.domain.manager

import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.usecases.UseCase

interface UsersUseCaseManager {
    fun authUseCase(
        name: String,
        password: String,
    ): UseCase<User>

    fun registerUseCase(
        name: String,
        password: String,
    ): UseCase<User>
}
