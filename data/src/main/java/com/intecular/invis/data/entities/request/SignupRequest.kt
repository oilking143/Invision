package com.intecular.invis.data.entities.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignupRequest(
    @Json(name = "Username") val userName: String,
    @Json(name = "Password") val password: String,
    @Json(name = "UserAttributes") val userAttributes: List<UserAttribute>
) : BaseRequest()
