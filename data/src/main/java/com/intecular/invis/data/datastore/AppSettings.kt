package com.intecular.invis.data.datastore

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val apiCustomHeader: String = "",
    val refreshToken: String = ""
)
