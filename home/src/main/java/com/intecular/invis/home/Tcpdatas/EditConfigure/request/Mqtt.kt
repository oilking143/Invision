package com.intecular.invis.home.Tcpdatas.EditConfigure.request

data class Mqtt(
    val enabled: Int,
    val mqtt_broker_url: String,
    val pass: String,
    val user: String
)