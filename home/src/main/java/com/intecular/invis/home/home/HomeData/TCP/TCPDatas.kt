package com.intecular.invis.home.home.HomeData.TCP

data class TCPDatas(
    val packetID: Int,
    val payload: Payload,
    val sn: String,
    val LocalHostName:String,
    val DeviceName:String
)

data class Payload(
    val callbackArgs: List<CallbackArgs>,
    val callbackName: Int
)

data class CallbackArgs(
    val temp_valid: Int,
    val aqi_valid: Int,
    val temp_celsius: String,
    val humidity: String,
    val BME680_temp_celsius: String,
    val BME680_humidity: String,
    val AQI: Int,
    val AQI_accuracy: Int,
    val pressure: Int,
    val gas: Int,
    val co2_equiv: Int,
    val bvoc_equiv: String,
    val lux_valid: Int,
    val lux: String,
    val occupancy_valid: Int,
    val occupancy_state: Int,
    val distance: Int,
    val movement_energy: Int,
    val stationary_energy: Int
)
