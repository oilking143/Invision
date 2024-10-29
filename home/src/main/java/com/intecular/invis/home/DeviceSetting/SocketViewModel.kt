package com.intecular.invis.home.DeviceSetting

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.intecular.invis.home.Api.ApiClient
import com.intecular.invis.home.Tcpdatas.Accessory.response.Accresponse
import com.intecular.invis.home.Tcpdatas.Configures.ConfigureResponse
import com.intecular.invis.home.Tcpdatas.DeviceInfo.InfoResponse
import com.intecular.invis.home.Tcpdatas.EditAccessory.response.editAcceryResponse
import com.intecular.invis.home.Tcpdatas.OTA.response.OtaSyncResponse
import com.intecular.invis.home.home.HomeData.TCP.CallbackArgs
import com.intecular.invis.home.home.HomeData.TCP.TCPDatas
import com.intecular.invis.home.home.Tcpdatas.deviceConfig.Response.Request.Payload
import com.intecular.invis.home.home.Tcpdatas.deviceConfig.Response.Request.deviceRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.net.Socket
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class SocketViewModel @Inject constructor(private val application: Application): ViewModel(){
    private var socket: Socket? = null
    private val _accresponseLiveData = MutableLiveData<Accresponse>()
    val accresponseLiveData: LiveData<Accresponse> get() = _accresponseLiveData

    private var _editaccresponseLiveData = MutableLiveData<editAcceryResponse>()
    val editaccresponseLiveData: LiveData<editAcceryResponse> get() = _editaccresponseLiveData

    private val _deviceInfoLiveData = MutableLiveData<InfoResponse>()
    val deviceInfoLiveData: LiveData<InfoResponse> get() = _deviceInfoLiveData

    private val _deviceConfigureLiveData = MutableLiveData<ConfigureResponse>()
    val deviceConfigureLiveData: LiveData<ConfigureResponse> get() = _deviceConfigureLiveData

    private val _deviceUpdateLiveData = MutableLiveData<OtaSyncResponse>()
    val deviceUpdateLiveData: LiveData<OtaSyncResponse> get() = _deviceUpdateLiveData



    fun getSocket(): Socket? {
        return socket
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun connectToService(hostName:String,deviceName:String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if(socket==null)
                {
                    socket = Socket(hostName, 3333)
                }

                val inputStream: InputStream = socket!!.getInputStream()
                val buffer = ByteArray(1024)
                var bytesRead: Int = inputStream.read(buffer)
                while (bytesRead > 0) {
                    val response = String(buffer, 0, bytesRead)
                    val responseJson = JSONObject(response)

                    checkResponse(responseJson)

                    bytesRead = inputStream.read(buffer)
                }

            } catch (e: UnknownHostException) {
                println("Unable to connect to server: ${e.message}")
            } catch (e: JSONException) {
                e.printStackTrace()
                println("Received invalid JSON format.")
            } catch (e: IOException) {
                println("Error reading/writing to socket: ${e.message}")
            }
        }
    }

    fun checkResponse(dnsJson:JSONObject){

        //SocketResponse{"sn":"11A1A7000B","packetID":839379,"payload":{"callbackName":22,"callbackArgs":[1,96]}}

        val jsonResponse = JsonParser.parseString(dnsJson.toString()).asJsonObject

        if(jsonResponse.has("PUBACK")){
            setPuback(dnsJson)
        }

        if(jsonResponse.has("callbackName")){
            Timber.d("SocketResponse$dnsJson")
        }
    }

    fun setPuback(dnsJson11: JSONObject){
        Timber.d("Socket_Puback==$dnsJson11")

        val packetID=dnsJson11.get("packetID")

        when(packetID){
            180072->{
                val gson = Gson()
                val accresponseType = object : TypeToken<Accresponse>() {}.type
                val accresponse: Accresponse = gson.fromJson(dnsJson11.toString(), accresponseType)
                _accresponseLiveData.postValue(accresponse)
            }

            8989889,950302->{
                val gson = Gson()
                val accresponseType = object : TypeToken<editAcceryResponse>() {}.type
                val accresponse: editAcceryResponse = gson.fromJson(dnsJson11.toString(), accresponseType)
                _editaccresponseLiveData.postValue(accresponse)
            }

            373184->{
                val gson = Gson()
                val accresponseType = object : TypeToken<InfoResponse>() {}.type
                val infoResponse: InfoResponse = gson.fromJson(dnsJson11.toString(), accresponseType)
                Timber.d("socket:${infoResponse.payload.callbackArgs.IM.MAC}")
                _deviceInfoLiveData.postValue(infoResponse)
            }

            819796->{
                val gson = Gson()
                val accresponseType = object : TypeToken<ConfigureResponse>() {}.type
                val configResponse: ConfigureResponse = gson.fromJson(dnsJson11.toString(), accresponseType)
                _deviceConfigureLiveData.postValue(configResponse)
            }

            414997->{

                val gson = Gson()
                val accresponseType = object : TypeToken<OtaSyncResponse>() {}.type
                var otaSyncResponse: OtaSyncResponse = gson.fromJson(dnsJson11.toString(), accresponseType)
                if(otaSyncResponse.payload!=null){
                    _deviceUpdateLiveData.postValue(otaSyncResponse)
                }
            }


        }
    }

    suspend fun getDevices()
    {
        val payload = Payload(callbackName = 12)
        val request = deviceRequest(packetID = 373184, payload = payload)
        Timber.d("request==$request")
        socket?.let { ApiClient().getDeviceInfo(request, it) }
    }
}