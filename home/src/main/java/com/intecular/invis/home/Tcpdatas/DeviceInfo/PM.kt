package com.intecular.invis.home.Tcpdatas.DeviceInfo

data class PM(
    val MAC: String,
    val fw_rev: String,
    val online: Boolean,
    val sn: String
)