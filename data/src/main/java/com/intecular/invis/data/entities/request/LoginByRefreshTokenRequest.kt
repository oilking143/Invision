package com.intecular.invis.data.entities.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginByRefreshTokenRequest(
    @Json(name = "AuthFlow") val authFlow: String = "REFRESH_TOKEN",
    @Json(name = "AuthParameters") val authParameters: LoginByRefreshParameters
) : BaseRequest()
