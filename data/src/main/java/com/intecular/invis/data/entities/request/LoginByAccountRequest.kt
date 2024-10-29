package com.intecular.invis.data.entities.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginByAccountRequest(
    @Json(name = "AuthFlow") val authFlow: String = "USER_PASSWORD_AUTH",
    @Json(name = "AuthParameters") val authParameters: LoginByAccountAuthParameters
) : BaseRequest()
