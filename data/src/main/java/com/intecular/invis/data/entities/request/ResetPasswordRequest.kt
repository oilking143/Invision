package com.intecular.invis.data.entities.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResetPasswordRequest(
    @Json(name = "Username") val userName: String,
    @Json(name = "Password") val newPassword: String,
    @Json(name = "ConfirmationCode") val confirmationCode: String
) : BaseRequest()
