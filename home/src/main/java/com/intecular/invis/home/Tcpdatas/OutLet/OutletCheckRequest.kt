package com.intecular.invis.home.Tcpdatas.OutLet

data class OutletCheckRequest(
    val packetID: Int,
    val payload: OutletPayload
)

data class OutletPayload(
    val callbackName: Int
)