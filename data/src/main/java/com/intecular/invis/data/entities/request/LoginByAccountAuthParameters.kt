package com.intecular.invis.data.entities.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginByAccountAuthParameters(
    @Json(name = "USERNAME") val userName: String = "",
    @Json(name = "PASSWORD") val password: String,
)
