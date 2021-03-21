package com.fndt.quote.domain.usecases

import com.fndt.quote.controllers.factory.PermissionException
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.PermissionValidator

abstract class RequestUseCase<T>(
    private val requestManager: RequestManager
) : UseCase<T>, PermissionValidator() {
    protected abstract val requestingUser: User?

    var isExecuted = false
        private set

    protected abstract suspend fun makeRequest(): T

    final override suspend fun run(): T = requestManager.execute {
        if (isExecuted) throw IllegalStateException("Executed more than 1 time")
        if (!validate(requestingUser)) throw PermissionException("Permission denied")
        makeRequest()
    }
}
