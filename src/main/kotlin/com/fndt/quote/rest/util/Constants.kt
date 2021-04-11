package com.fndt.quote.rest.util

const val ID = "id"
const val QUOTE_ID = "id"

const val QUOTES_ENDPOINT = "/quotes"
const val LIKE_ENDPOINT = "/like"
const val COMMENTS_ENDPOINT = "$QUOTES_ENDPOINT/{$ID}/comment"
const val REGISTRATION_ENDPOINT = "/register"
const val TAG_ENDPOINT = "/tag"
const val ROLE_ENDPOINT = "/role"
const val REVIEW_ENDPOINT = "/approve"
const val REVIEW_QUOTE_ENDPOINT = "$QUOTES_ENDPOINT$REVIEW_ENDPOINT"
const val BAN_ENDPOINT = "/ban/{$ID}"
const val ADD_ENDPOINT = "/add"
const val AVATAR_ENDPOINT = "/avatar"
const val REVIEW_TAG_ENDPOINT = "$TAG_ENDPOINT$REVIEW_ENDPOINT"
const val PERMANENT_BAN_ENDPOINT = "$BAN_ENDPOINT/permanent"