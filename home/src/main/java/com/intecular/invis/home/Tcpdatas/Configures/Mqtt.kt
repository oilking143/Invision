package com.intecular.invis.home.Tcpdatas.Configures

data class Mqtt(
    var enabled: Int,
    var mqtt_broker_url: String,
    var pass: String,
    var qos: Int,
    var user: String
)