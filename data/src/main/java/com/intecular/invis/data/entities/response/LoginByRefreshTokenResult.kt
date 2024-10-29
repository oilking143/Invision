package com.intecular.invis.data.entities.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginByRefreshTokenResult(
    @Json(name = "AccessToken") val accessToken: String,
    @Json(name = "ExpiresIn") val expiresIn: Long,
    @Json(name = "IdToken") val idToken: String,
    @Json(name = "TokenType") val tokenType: String
)
