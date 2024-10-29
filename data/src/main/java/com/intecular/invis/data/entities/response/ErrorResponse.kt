package com.intecular.invis.data.entities.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "__type") val type: String,
    @Json(name = "message") val message: String
)
