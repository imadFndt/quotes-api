package com.fndt.quote.domain.usecases

import com.fndt.quote.domain.PermissionException
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.dto.User

abstract class RequestUseCase<T>(
    private val requestManager: RequestManager
) : UseCase<T> {
    protected abstract val requestingUser: User?

    var isExecuted = false
        private set

    protected abstract suspend fun makeRequest(): T

    protected abstract fun validate(user: User?): Boolean

    final override suspend fun run(): T = requestManager.execute {
        if (isExecuted) throw IllegalStateException("Executed more than 1 time")
        if (!validate(requestingUser)) throw PermissionException("Permission denied")
        makeRequest().also { isExecuted = true }
    }
}
