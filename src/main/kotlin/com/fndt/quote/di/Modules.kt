package com.fndt.quote.di

import com.fndt.quote.controllers.AuthController
import com.fndt.quote.controllers.SelectionsController
import com.fndt.quote.controllers.QuotesController
import com.fndt.quote.controllers.UserController
import com.fndt.quote.controllers.factory.CommentsUseCaseFactory
import com.fndt.quote.controllers.factory.SelectionUseCaseFactory
import com.fndt.quote.controllers.factory.QuotesUseCaseFactory
import com.fndt.quote.controllers.factory.UsersUseCaseFactory
import com.fndt.quote.data.*
import com.fndt.quote.data.QuoteFilterImpl
import com.fndt.quote.domain.QuoteFilter
import com.fndt.quote.domain.RequestManager
import com.fndt.quote.domain.manager.PermissionManager
import com.fndt.quote.domain.manager.UsersUseCaseManager
import com.fndt.quote.domain.manager.implementations.PermissionManagerImpl
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
        factory <RequestManager> { RequestManagerImpl() }
        single<UsersUseCaseManager> { UsersUseCaseFactory(get(), get(), get()) }
        single<QuoteFilter.Builder> { QuoteFilterImpl.Companion.FilterBuilder(get()) }
    }
    val useCaseManagerModule = module {
        factory { QuotesUseCaseFactory(get(), get(), get(), get(), get(), get()) }
        factory { CommentsUseCaseFactory(get(), get(), get(), get()) }
        factory { SelectionUseCaseFactory(get(), get(), get()) }
        factory { UsersUseCaseFactory(get(), get(), get()) }
    }

    val controllersModule = module {
        single { AuthController(get()) }
        single { QuotesController(get()) }
        single { UserController(get()) }
        single { SelectionsController(get()) }
    }
}
