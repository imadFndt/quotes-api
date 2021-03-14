package com.fndt.quote.data

open class QuotesTablesProvider(dbProvider: DatabaseDefinition) {
    protected val quotesTable: DatabaseDefinition.Quotes by dbProvider
    protected val authorsTable: DatabaseDefinition.Authors by dbProvider
    protected val tagsTable: DatabaseDefinition.Tags by dbProvider
    protected val tagQuoteMapTable: DatabaseDefinition.TagsOnQuotes by dbProvider
    protected val commentsTable: DatabaseDefinition.Comments by dbProvider
    protected val likesQuotesMapTable: DatabaseDefinition.LikesOnQuotes by dbProvider
    protected val usersTable: DatabaseDefinition.Users by dbProvider
}
