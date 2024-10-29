package com.intecular.invis.home.Tcpdatas.Accessory.response

data class Accresponse(
    val PUBACK: Int,
    val packetID: Int,
    val payload: List<Payload>,
    val sn: String
)

data class Payload(
    val accessory: Int,
    val name: String
)

