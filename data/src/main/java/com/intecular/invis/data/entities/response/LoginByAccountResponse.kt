package com.intecular.invis.data.entities.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginByAccountResponse(
    @Json(name = "AuthenticationResult") val authenticationResult: LoginByAccountResult
)
