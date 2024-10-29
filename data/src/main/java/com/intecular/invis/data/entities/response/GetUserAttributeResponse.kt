package com.intecular.invis.data.entities.response

import com.intecular.invis.data.entities.request.UserAttribute
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUserAttributeResponse(
    @Json(name = "UserAttributes") val userAttributesList: List<UserAttribute>,
    @Json(name = "Username") val userName: String
)
