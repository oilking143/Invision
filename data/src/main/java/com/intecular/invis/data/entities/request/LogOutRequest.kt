package com.intecular.invis.data.entities.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LogOutRequest(
    @Json(name="Token")val refreshToken: String
) : BaseRequest()
