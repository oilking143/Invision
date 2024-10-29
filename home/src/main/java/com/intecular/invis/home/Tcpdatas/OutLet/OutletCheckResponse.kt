package com.intecular.invis.home.Tcpdatas.OutLet

data class OutletCheckResponse(
    val PUBACK: Int,
    val packetID: Int,
    val payload: Payload,
    val sn: String
)

data class Payload(
    val callbackArgs: List<Int>,
    val callbackName: Int
)