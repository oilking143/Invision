package com.intecular.invis.data.entities.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
open class BaseRequest(
   @property:Json(name = "ClientId") var clientId: String = "5lap6viltlf6ts1in88nq1eqhc"
)

