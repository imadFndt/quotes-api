package com.fndt.quote.domain.usecases.review

import com.fndt.quote.domain.UseCaseAdapter
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.RequestManager
import com.fndt.quote.domain.usecases.base.RequestUseCase

class ReviewUseCase<T>(
    private val decision: Boolean,
    private val adapter: UseCaseAdapter<T>,
    override val requestingUser: User?,
    requestManager: RequestManager
) : RequestUseCase<Unit>(requestManager) {
    override fun validate(user: User?) = adapter.hasPermissions(user)

    override suspend fun makeRequest() {
        val item = adapter.getItem() ?: throw IllegalStateException("Item not found")
        if (decision) adapter.addItem(item) else adapter.removeItem(item)
    }
}
