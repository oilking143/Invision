package com.intecular.invis.data.entities.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChangePasswordRequest(
    @Json(name = "AccessToken") val accessToken: String = "",
    @Json(name = "PreviousPassword") val previousPassword: String,
    @Json(name = "ProposedPassword") val proposedPassword: String
)
