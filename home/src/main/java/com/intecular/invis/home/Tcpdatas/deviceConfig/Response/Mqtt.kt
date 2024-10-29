package com.intecular.invis.home.Tcpdatas.deviceConfig.Response

data class Mqtt(
    val enabled: Int,
    val mqtt_broker_url: String,
    val pass: String,
    val qos: Int,
    val user: String
)