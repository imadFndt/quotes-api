package com.fndt.quote.data

import com.fndt.quote.domain.manager.RepositoryProvider
import com.fndt.quote.domain.repository.*
import com.fndt.quote.domain.repository.base.BaseRepository
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
class RepositoryProviderImpl(private val databaseProvider: DatabaseProvider) : RepositoryProvider {
    override fun <T : BaseRepository> getRepository(k: KClass<T>): T {
        return when (k) {
            AuthorRepository::class -> AuthorRepositoryImpl(databaseProvider) as T
            CommentRepository::class -> CommentRepositoryImpl(databaseProvider) as T
            LikeRepository::class -> LikeRepositoryImpl(databaseProvider) as T
            QuoteRepository::class -> QuoteRepositoryImpl(databaseProvider) as T
            TagRepository::class -> TagRepositoryImpl(databaseProvider) as T
            TagSelectionRepository::class -> TagSelectionRepositoryImpl(databaseProvider) as T
            UserRepository::class -> UserRepositoryImpl(databaseProvider) as T
            else -> throw IllegalArgumentException("Bad repository: $k")
        }
    }
}
