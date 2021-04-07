package com.fndt.quote.domain.usecases.add

import com.fndt.quote.domain.UseCaseAdapter
import com.fndt.quote.domain.dto.User
import com.fndt.quote.domain.repository.base.SimpleRepository

class SimpleRepositoryAdapter<T>(
    private val repository: SimpleRepository<T>,
    private val hasPermission: (User?) -> Boolean,
    private val createItem: (User) -> T?
) : UseCaseAdapter<T> {
    lateinit var user: User

    override fun hasPermissions(user: User?): Boolean {
        user?.let { this.user = user }
        return hasPermission(user)
    }

    override fun getItem(): T? {
        return createItem(user)
    }

    override fun addItem(item: T) {
        repository.add(item)
    }

    override fun removeItem(item: T) {
        repository.remove(item)
    }
}
