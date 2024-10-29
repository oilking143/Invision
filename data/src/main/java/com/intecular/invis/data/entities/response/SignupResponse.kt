package com.intecular.invis.data.entities.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignupResponse(
    @Json(name = "CodeDeliveryDetails") val codeDeliveryDetails: CodeDeliveryDetails?,
    @Json(name = "UserConfirmed") val userConfirmStatus: Boolean? = false,
    @Json(name = "UserSub") val userSub: String?
)
