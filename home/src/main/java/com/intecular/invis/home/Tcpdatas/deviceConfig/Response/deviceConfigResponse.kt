package com.intecular.invis.home.Tcpdatas.deviceConfig.Response

data class deviceConfigResponse(
    val PUBACK: Int,
    val packetID: Int,
    val payload: Payload,
    val sn: String
)