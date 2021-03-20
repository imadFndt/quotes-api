package com.fndt.quote.domain.usecases

import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionValidator
import com.fndt.quote.domain.manager.implementations.PermissionException

abstract class RequestUseCase<T>(
    private val requestManager: RequestManager
) : UseCase<T>, PermissionValidator() {
    protected abstract val requestingUser: User?

    protected abstract suspend fun makeRequest(): T

    final override suspend fun run(): T = requestManager.execute {
        if (!validate(requestingUser)) throw PermissionException("Permission denied")
        makeRequest()
    }
}
