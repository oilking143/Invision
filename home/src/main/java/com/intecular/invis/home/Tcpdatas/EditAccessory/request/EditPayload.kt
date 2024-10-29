package com.intecular.invis.home.Tcpdatas.EditAccessory.request

import com.google.gson.annotations.SerializedName

data class EditPayload(
    val callbackName: Int,
    val callbackArgs: List<CallbackArg>

)