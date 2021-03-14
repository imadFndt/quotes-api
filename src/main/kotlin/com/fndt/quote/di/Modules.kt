package com.fndt.quote.di

import com.fndt.quote.controllers.AuthController
import com.fndt.quote.controllers.AuthorsController
import com.fndt.quote.controllers.QuotesController
import com.fndt.quote.controllers.RegistrationController
import com.fndt.quote.data.*
import com.fndt.quote.domain.AuthService
import com.fndt.quote.domain.QuotesEditService
import com.fndt.quote.domain.RegistrationService
import org.koin.dsl.module

object Modules {
    val dbModule = module {
        DatabaseDefinition.initDb
        single { DatabaseDefinition.Quotes }
        single { DatabaseDefinition.Users }
        single { DatabaseDefinition.Authors }
        single { DatabaseDefinition.Tags }
        single { DatabaseDefinition.TagsOnQuotes }
        single { DatabaseDefinition.Comments }
        single { DatabaseDefinition.LikesOnQuotes }
    }
    val serviceModule = module {
        single<QuotesEditService> { EditServiceImpl(get(), get()) }
        single<QuotesBrowseService> { QuotesBrowseServiceImpl(get(), get(), get(), get(), get(), get()) }
        single<RegistrationService> { RegistrationServiceImpl(get()) }
        single<AuthService> { AuthServiceImpl(get()) }
    }
    val controllersModule = module {
        single { AuthController(get()) }
        single { AuthorsController(get()) }
        single { QuotesController(get(), get()) }
        single { RegistrationController(get()) }
    }
}
