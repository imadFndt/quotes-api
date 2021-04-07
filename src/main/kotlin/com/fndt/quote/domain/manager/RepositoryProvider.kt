package com.fndt.quote.domain.manager

import com.fndt.quote.domain.repository.base.BaseRepository
import kotlin.reflect.KClass

interface RepositoryProvider {
    fun <T : BaseRepository> getRepository(k: KClass<T>): T
}

inline fun <reified T : BaseRepository> RepositoryProvider.getRepository(): T {
    return getRepository(T::class)
}
