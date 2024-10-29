package com.intecular.invis.home.Tcpdatas.deviceConfig.Response

data class AccPrefs(
    val adaptiveNightlightFeature: Int,
    val adaptiveSensitivity: Int,
    val aqiColorRGBFeature: Int,
    val capacitiveCtrl: Int,
    val motionAwayFeature: Int,
    val outletPwrIndicatorOn: Int,
    val pmIndicatorBrightness: Int
)