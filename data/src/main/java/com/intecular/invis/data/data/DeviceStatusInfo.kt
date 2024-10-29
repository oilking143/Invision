package com.intecular.invis.data.data

import androidx.compose.ui.graphics.Color


data class DeviceStatusInfo(
    val borderColorId: Color,
    val borderWidth: Float = 0.5f,
    val deviceStatusIconId: Int,
    val deviceStatusContent: String
)
