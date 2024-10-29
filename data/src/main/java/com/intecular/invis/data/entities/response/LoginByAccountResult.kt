package com.intecular.invis.data.entities.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginByAccountResult(
    @Json(name = "AccessToken") val accessToken: String,
    @Json(name = "ExpiresIn") val expiresIn: Long,
    @Json(name = "IdToken") val idToken: String,
    @Json(name = "RefreshToken") val refreshToken: String,
    @Json(name = "TokenType") val tokenType: String
)
