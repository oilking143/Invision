package com.intecular.invis.home.home.Tcpdatas.deviceConfig.Response.Request

data class deviceRequest(
    val packetID: Int,
    val payload: Payload
)

data class Payload(
    val callbackName: Int
)