package com.intecular.invis.home.Tcpdatas.DeviceInfo

data class InfoResponse(
    val PUBACK: Int,
    val packetID: Int,
    val payload: Payload,
    val sn: String
)