package com.intecular.invis.home.Tcpdatas.EditAccessory.response

data class editAcceryResponse(
    var PUBACK: Int,
    val packetID: Int,
    val sn: String
)