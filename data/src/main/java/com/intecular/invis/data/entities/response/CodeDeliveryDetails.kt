package com.intecular.invis.data.entities.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CodeDeliveryDetails(
    @Json(name = "AttributeName") val attributeName: String,
    @Json(name = "DeliveryMedium") val deliveryMedium: String,
    @Json(name = "Destination") val destination: String
)
