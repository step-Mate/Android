package com.stepmate.data.remote.dto.response

data class Token(
    val refreshToken : String,
    val accessToken : String
)

data class AccessToken(
    val accessToken : String
)