package com.intecular.invis.data.entities.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConfirmSignupRequest(
    @Json(name = "ConfirmationCode") val confirmationCode: String,
    @Json(name = "Username") val userName: String,
) : BaseRequest()
