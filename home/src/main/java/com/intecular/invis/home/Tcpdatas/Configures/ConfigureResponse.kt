package com.intecular.invis.home.Tcpdatas.Configures

data class ConfigureResponse(
    val PUBACK: Int,
    val packetID: Int,
    val payload: Payload,
    val sn: String
)