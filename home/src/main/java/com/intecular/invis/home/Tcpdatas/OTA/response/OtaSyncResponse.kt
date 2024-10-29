package com.intecular.invis.home.Tcpdatas.OTA.response

data class OtaSyncResponse(
    val packetID: Int,
    val payload: Payload,
    val sn: String
)