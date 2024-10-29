package com.intecular.invis.data.entities.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserAttribute(
    @Json(name = "Name") val attributeName: String,
    @Json(name = "Value") val attributeValue: String
)
