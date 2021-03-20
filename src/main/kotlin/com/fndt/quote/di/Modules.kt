package com.fndt.quote.di

import com.fndt.quote.controllers.AuthController
import com.fndt.quote.controllers.QuotesController
import com.fndt.quote.controllers.RegistrationController
import com.fndt.quote.data.*
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.manager.CommentsUseCaseManager
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.manager.QuotesUseCaseManager
import com.fndt.quote.domain.manager.UsersUseCaseManager
import com.fndt.quote.domain.manager.implementations.CommentsUseCaseManagerImpl
import com.fndt.quote.domain.manager.implementations.PermissionManagerImpl
import com.fndt.quote.domain.manager.implementations.QuotesUseCaseManagerImpl
import com.fndt.quote.domain.manager.implementations.UsersUseCaseManagerImpl
import com.fndt.quote.domain.repository.*
import org.koin.dsl.module

object Modules {
    val dbModule = module {
        DatabaseProvider.initDb
        single { DatabaseProvider }
        single<CommentRepository> { CommentRepositoryImpl(get()) }
        single<LikeRepository> { LikeRepositoryImpl(get()) }
        single<QuoteRepository> { QuoteRepositoryImpl(get()) }
        single<TagRepository> { TagRepositoryImpl(get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }
    }
    val managerModule = module {
        single<PermissionManager> { PermissionManagerImpl() }
        single<RequestManager> { RequestManagerImpl() }
        single<UsersUseCaseManager> { UsersUseCaseManagerImpl(get(), get(), get()) }
    }
    val useCaseManagerModule = module {
        factory<QuotesUseCaseManager> { QuotesUseCaseManagerImpl(get(), get(), get(), get(), get()) }
        factory<CommentsUseCaseManager> { CommentsUseCaseManagerImpl(get(), get(), get(), get()) }
    }

    val controllersModule = module {
        single { AuthController(get()) }
        single { QuotesController(get(), get()) }
        single { RegistrationController(get()) }
    }
}
