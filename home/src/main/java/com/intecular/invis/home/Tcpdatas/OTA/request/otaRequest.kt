package com.intecular.invis.home.Tcpdatas.OTA.request

data class otaRequest(
    val packetID: Int,
    val payload: Payload
)