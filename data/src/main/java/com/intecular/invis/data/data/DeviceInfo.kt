package com.intecular.invis.data.data

data class DeviceInfo(
    var status: Int,
    val deviceName: String,
    val motionDetected: Int,
    val occupied: Int,
    val distance: Int,
    val airQualityIndex: String,
    val temperature: Float,
    val humidity: String,
    val lux: String,
    val voc: String,
    val co2: Int,
    val pressure: Int
)
