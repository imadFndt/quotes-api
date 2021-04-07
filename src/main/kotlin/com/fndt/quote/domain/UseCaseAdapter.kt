package com.fndt.quote.domain

import com.fndt.quote.domain.dto.User

interface UseCaseAdapter<T> {
    fun hasPermissions(user: User?): Boolean
    fun getItem(): T?
    fun addItem(item: T)
    fun removeItem(item: T) {
    }
}
