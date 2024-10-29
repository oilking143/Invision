package com.intecular.invis.home.home

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.intecular.invis.data.RoomInfoUiState
import com.intecular.invis.data.data.DeviceHomeDrawerInfo
import com.intecular.invis.data.data.DeviceRoomData
import com.intecular.invis.home.Api.ApiClient
import com.intecular.invis.home.Tcpdatas.DeviceInfo.InfoResponse
import com.intecular.invis.home.Tcpdatas.OutLet.OutletCheckRequest
import com.intecular.invis.home.Tcpdatas.OutLet.OutletCheckResponse
import com.intecular.invis.home.commissioning.CommissioningUtil
import com.intecular.invis.home.home.HomeData.OutLet.outletRequest
import com.intecular.invis.home.home.HomeData.TCP.CallbackArgs
import com.intecular.invis.home.home.HomeData.TCP.TCPDatas
import com.intecular.invis.home.home.HomeData.nightLight.Payload
import com.intecular.invis.home.home.HomeData.nightLight.nightLightData
import com.intecular.invis.home.home.Tcpdatas.deviceConfig.Response.Request.deviceRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.Socket
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val commissioningUtil: CommissioningUtil,
    val application: Application
) : ViewModel() {

    var deviceRoomInfo by mutableStateOf(RoomInfoUiState(emptyList(), emptyList()))
        private set
    /**=============================Socket Connection====================================**/
    var TAG = "NsdHelper"
    var homeSocket: Socket? = null //Socket不可以斷掉，所以要全域宣告
    var PacketId=0
    @SuppressLint("StaticFieldLeak")
    val context = application.applicationContext
    val nsdManager: NsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    var mDnsInfo by mutableStateOf(mutableListOf<NsdServiceInfo>())
        private set
    fun getSocket(): Socket? {
        return homeSocket
    }
    private val _deviceInfoLiveData = MutableLiveData<InfoResponse>()
    val deviceInfoLiveData: LiveData<InfoResponse> get() = _deviceInfoLiveData

    private val _outletInfoLiveData = MutableLiveData<OutletCheckResponse>()
    val outletInfoLiveData: LiveData<OutletCheckResponse> get() = _outletInfoLiveData

    private val _showDialog = MutableLiveData(false)
    val showDialog: LiveData<Boolean> get() = _showDialog
    val deviceList: MutableList<com.intecular.invis.data.data.DeviceInfo> = mutableListOf()
    var _roomData by mutableStateOf<List<DeviceRoomData>>(emptyList())
        private set
    private val _roomDataLiveData = MutableLiveData<List<DeviceRoomData>>()
    val roomDataLiveData: LiveData<List<DeviceRoomData>> = _roomDataLiveData
    /**==================================================================================**/





    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun setDnsInfo(DNSInfo: NsdServiceInfo) {
        val isDuplicate = mDnsInfo.any { it.hostAddresses[0] == DNSInfo.hostAddresses[0] }
        if(!isDuplicate){
            mDnsInfo.add(DNSInfo)
        }else{
            for(i in mDnsInfo.indices){

                if(mDnsInfo[i].hostAddresses[0]==DNSInfo.hostAddresses[0])
                {
                    mDnsInfo.removeAt(i)
                    mDnsInfo.add(i,DNSInfo)
                }
            }
        }

        for(i in mDnsInfo.indices){
            val host = mDnsInfo[i].hostAddresses[0].toString()
            Timber.d("SocketHost==$host")
            val name =mDnsInfo[i].serviceName
            connectToService(host.replace(Regex("[^\\d.]"), ""),name)
            Thread.sleep(500)
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun connectToService(hostName:String,deviceName:String) {
        CoroutineScope(Dispatchers.IO).launch {

            try {
                if(homeSocket==null)
                {
                    homeSocket = Socket(hostName, 3333)
                    getDevices(homeSocket!!)
                }

                val inputStream: InputStream = homeSocket!!.getInputStream()
                val buffer = ByteArray(1024)
                var bytesRead: Int = inputStream.read(buffer)
                while (bytesRead > 0) {
                    val response = String(buffer, 0, bytesRead)
                    val responseJson = JSONObject(response)
                    responseJson.put("LocalHostName", hostName)
                    responseJson.put("DeviceName", deviceName)
                    checkResponse(responseJson)

                    bytesRead = inputStream.read(buffer)
                }

            } catch (e: UnknownHostException) {
                println("Unable to connect to server: ${e.message}")
                onDialogOpen()
            } catch (e: JSONException) {
                e.printStackTrace()
                println("Received invalid JSON format.")
            } catch (e: IOException) {
                println("Error reading/writing to socket: ${e.message}")
            }
        }
    }

    fun checkResponse(dnsJson:JSONObject){
        Timber.d("allResponse$dnsJson")
        val jsonResponse = JsonParser.parseString(dnsJson.toString()).asJsonObject
        if(jsonResponse.has("PUBACK")){
            setPuback(dnsJson)
        }else if(jsonResponse.get("payload").asJsonObject.has("callbackName")
            &&jsonResponse.get("payload").asJsonObject.get("callbackName").asInt==11) {
            setDnsJson(dnsJson)
        }
    }

    fun setDnsJson(dnsJson11:JSONObject){

        val gson = Gson()
        val jsonObject = gson.fromJson(dnsJson11.toString(), JsonObject::class.java)

        val sn = jsonObject.get("sn").asString
        val packetID = jsonObject.get("packetID").asInt
        val payloadJson = jsonObject.getAsJsonObject("payload")
        val LocalHostName = jsonObject.get("LocalHostName").asString
        val deviceName = jsonObject.get("DeviceName").asString

        val callbackName = payloadJson.get("callbackName").asInt
        val callbackArgsJson = payloadJson.getAsJsonArray("callbackArgs")
        val callbackArgs = mutableListOf<CallbackArgs>()

        for (jsonElement in callbackArgsJson) {
            if (jsonElement is JsonObject) {
                val args = gson.fromJson(jsonElement, CallbackArgs::class.java)
                callbackArgs.add(args)
            } else {
                Timber.d("convertFail")
            }
        }
        val payload = com.intecular.invis.home.home.HomeData.TCP.Payload(callbackArgs,callbackName)

        deviceRoomInfo =  initDeviceItem(TCPDatas(packetID, payload,sn,LocalHostName,deviceName))
        _roomDataLiveData.postValue(_roomData)

    }

    fun setPuback(dnsJson11:JSONObject){
        Timber.d("Puback==$dnsJson11")

        val packetID=dnsJson11.get("packetID")

        when(packetID){

            373184-> {
                val gson = Gson()
                val accresponseType = object : TypeToken<InfoResponse>() {}.type
                val infoResponse: InfoResponse =
                    gson.fromJson(dnsJson11.toString(), accresponseType)
                _deviceInfoLiveData.postValue(infoResponse)
                if (infoResponse.payload.callbackArgs.PM.online) {
                    viewModelScope.launch {
                            setOutLetStatus(1,1)
                            delay(100)
                            setOutLetStatus(2,1)
                            delay(100)
                            setnightLightStatus(1,100)
                            delay(100)
                    }
                }
            }

            369953->{
                val gson = Gson()
                val accresponseType = object : TypeToken<InfoResponse>() {}.type
                val outletResponse: OutletCheckResponse = gson.fromJson(dnsJson11.toString(), accresponseType)
                _outletInfoLiveData.postValue(outletResponse)
                setOutLetStatus(1,outletResponse.payload.callbackArgs[0])
                setOutLetStatus(2,outletResponse.payload.callbackArgs[1])
            }

        }
    }

    fun initDeviceItem(topics: TCPDatas): RoomInfoUiState {
        var args = topics.payload.callbackArgs
        val deviceName = topics.DeviceName
        if(deviceName.contains("Deco"))
        {
            deviceName.replace("Deco","Outlet")
        }

            val arg = args[0]
            val info = com.intecular.invis.data.data.DeviceInfo(
                1,
                deviceName,
                1,
                arg.occupancy_state,
                arg.distance,
                checkAQILebel(arg.AQI),
                convertTemp(arg.BME680_temp_celsius),
                arg.humidity,
                Luxchecker(arg.lux),
                arg.bvoc_equiv,
                arg.co2_equiv,
                arg.pressure
            )

           updateList(deviceList,info)

            val data = DeviceRoomData(
                deviceName,
                deviceList
            )


        updateRoomData(data)


        val drawer = listOf(DeviceHomeDrawerInfo("First Home"), DeviceHomeDrawerInfo("Second Home"))
        val state = RoomInfoUiState(drawer,_roomData)


        return state
    }
    private fun updateList(list: MutableList<com.intecular.invis.data.data.DeviceInfo>, newDeviceInfo: com.intecular.invis.data.data.DeviceInfo) {
        val existingIndex = list.indexOfFirst { it.deviceName == newDeviceInfo.deviceName }

        if (existingIndex != -1) {
            list[existingIndex] = newDeviceInfo
        } else {
            list.add(newDeviceInfo)
        }
    }

    fun updateRoomData(data: DeviceRoomData) {
        _roomData = if (_roomData.isNotEmpty()) {
            _roomData.toMutableList().apply {
                this[0] = data.copy(deviceList = data.deviceList.toList())
            }
        } else {
            _roomData + data.copy(deviceList = data.deviceList.toList())
        }
    }

    fun convertTemp(celsiusString: String): Float {
        val celsius = celsiusString.toFloatOrNull() ?: return Float.NaN // 如果轉換失敗，返回NaN
        val fahrenheit = (celsius * 9 / 5) + 32
        return fahrenheit
    }
    fun checkAQILebel(AQI:Int):String{
        if(AQI<40){
            return "AQI $AQI\n\rExcellent"
        }else if(AQI in 41..55){
            return "AQI $AQI\n\rGood"
        }else if(AQI in 56..125){
            return "AQI $AQI\n\rAverage"
        }else if(AQI in 126..175){
            return "AQI $AQI\n\rPoor"
        }else{
            return "AQI $AQI \n\rHazardous"
        }

    }

    fun Luxchecker(Luxvalue:String):String{
        if(Luxvalue=="inf"){
            return "0"
        }else{
            return Luxvalue
        }
    }

    //NightLight Turn on/off
    fun setnightLightStatus(switch:Int,percent:Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val callbackArg= listOf(switch,percent)
                val payload = Payload(callbackName = 14, callbackArgs = callbackArg)
                PacketId=getCurrentTimestampFormatted()
                val nightLightRequest=nightLightData(packetID = PacketId, payload = payload)

                try {
                    val gson = Gson()
                    val jsonString = gson.toJson(nightLightRequest)
                    val outputStream: OutputStream =

                        homeSocket!!.getOutputStream()

                    // Send a message to the server
                    val message: String = jsonString
                    Timber.d("message = $message")
                    outputStream.write(message.toByteArray())
                    outputStream.flush()
                } catch (e: UnknownHostException) {
                    println("Unable to connect to server: ${e.message}")
                    onDialogOpen()
                } catch (e: IOException) {
                    println("Error reading/writing to socket: ${e.message}")
                } catch (e: NullPointerException) {
                    println("Error reading/writing to socket: ${e.message}")
                }

            }

        }

    }

    //NightLight Turn on/off
    fun setOutLetStatus(Outlet:Int,status:Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                val callbackArg= listOf(Outlet,status)
                val payload = Payload(callbackName = 10, callbackArgs = callbackArg)
                PacketId=getCurrentTimestampFormatted()
                val nightLightRequest=nightLightData(packetID = PacketId, payload = payload)

                try {
                    val gson = Gson()
                    val jsonString = gson.toJson(nightLightRequest)
                    val outputStream: OutputStream =

                        homeSocket!!.getOutputStream()

                    // Send a message to the server
                    val message: String = jsonString
                    Timber.d("message = $message")
                    outputStream.write(message.toByteArray())
                    outputStream.flush()

                } catch (e: UnknownHostException) {
                    println("Unable to connect to server: ${e.message}")
                    onDialogOpen()
                } catch (e: IOException) {
                    println("Error reading/writing to socket: ${e.message}")
                }

            }
        }

    }


    //產出PacketID
    fun getCurrentTimestampFormatted(): Int {
        val currentTimeMillis = System.currentTimeMillis()
        val date = Date(currentTimeMillis)
        val sdf = SimpleDateFormat("MMddHHmm", Locale.getDefault())
        return sdf.format(date).toInt()
    }

    //取得Device資訊
    suspend fun getDevices(socket: Socket)
    {
        val payload = com.intecular.invis.home.home.Tcpdatas.deviceConfig.Response.Request.Payload(callbackName = 12)
        val request = deviceRequest(packetID = 373184, payload = payload)
        ApiClient().getDeviceInfo(request, socket)
    }

     fun syncOutlet()
    {        viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val payload = com.intecular.invis.home.Tcpdatas.OutLet.OutletPayload(callbackName = 9)
            val request = OutletCheckRequest(packetID = 369953, payload = payload)
            ApiClient().getOutletStatus(request, homeSocket!!)
        }
    }

    }

    //無法連上時要跳出wifi警告
    fun onDialogOpen() {
        _showDialog.postValue(true)
    }

    fun onDialogDismiss() {
        _showDialog.postValue(false)
    }

}



