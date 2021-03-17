package com.fndt.quote.di

import com.fndt.quote.controllers.AuthController
import com.fndt.quote.controllers.QuotesController
import com.fndt.quote.controllers.RegistrationController
import com.fndt.quote.data.*
import com.fndt.quote.domain.ServiceFactory
import com.fndt.quote.domain.ServiceHolder
import com.fndt.quote.domain.dao.*
import org.koin.dsl.module

object Modules {
    val dbModule = module {
        DatabaseProvider.initDb
        single { DatabaseProvider }
        single<CommentDao> { CommentDaoImpl(get()) }
        single<LikeDao> { LikeDaoImpl(get()) }
        single<QuoteDao> { QuoteDaoImpl(get()) }
        single<TagDao> { TagDaoImpl(get()) }
        single<UserDao> { UserDaoImpl(get()) }
        single<AuthorDao> { AuthorDaoImpl(get()) }
    }
    val serviceModule = module {
        single { ServiceHolder(ServiceFactory(get(), get(), get(), get(), get(), get())) }
    }

    val controllersModule = module {
        single { AuthController(get<ServiceHolder>().authService) }
        single { QuotesController(get()) }
        single { RegistrationController(get<ServiceHolder>().registrationService) }
    }
}
