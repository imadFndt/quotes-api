package com.fndt.quote.domain.usecases.add

import com.fndt.quote.domain.UseCaseAdapter
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.manager.RequestManager
import com.fndt.quote.domain.usecases.base.RequestUseCase

class AddUseCase<T : Any>(
    private val adapter: UseCaseAdapter<T>,
    override val requestingUser: User,
    requestManager: RequestManager
) : RequestUseCase<Unit>(requestManager) {

    lateinit var item: T

    override fun validate(user: User?) = adapter.hasPermissions(user)

    override suspend fun makeRequest() {
        item = adapter.getItem() ?: throw IllegalStateException("Failed to get item")
        adapter.addItem(item)
    }
}
