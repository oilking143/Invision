package com.intecular.invis.home.Tcpdatas.Accessory.request

data class accRequest(
    val packetID: Int,
    val payload: Payload
)

data class Payload(
    val callbackName: Int
)