package com.intecular.invis.home.Api

import com.google.gson.Gson
import com.intecular.invis.home.DeviceSetting.SocketViewModel
import com.intecular.invis.home.Tcpdatas.Accessory.request.accRequest
import com.intecular.invis.home.Tcpdatas.EditAccessory.request.editAccessRequest
import com.intecular.invis.home.Tcpdatas.EditConfigure.request.EditRequest
import com.intecular.invis.home.Tcpdatas.OTA.request.otaRequest
import com.intecular.invis.home.Tcpdatas.OutLet.OutletCheckRequest
import com.intecular.invis.home.home.Tcpdatas.deviceConfig.Response.Request.deviceRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.io.OutputStream
import java.net.Socket
import java.net.UnknownHostException

class ApiClient() {

    suspend fun getDeviceInfo(infoRequest: deviceRequest, socket: Socket) {
        withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val jsonString = gson.toJson(infoRequest)
                val outputStream: OutputStream =

                    socket.getOutputStream()


                // Send a message to the server
                val message: String = jsonString
                Timber.d("message = $message")
                outputStream.write(message.toByteArray())
                outputStream.flush()

            } catch (e: UnknownHostException) {
                println("Unable to connect to server: ${e.message}")
            } catch (e: IOException) {
                println("Error reading/writing to socket: ${e.message}")
            }
        }
    }

    suspend fun getOutletStatus(outletRequest: OutletCheckRequest, socket: Socket) {
        withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val jsonString = gson.toJson(outletRequest)
                val outputStream: OutputStream =

                    socket.getOutputStream()


                // Send a message to the server
                val message: String = jsonString
                Timber.d("message = $message")
                outputStream.write(message.toByteArray())
                outputStream.flush()

            } catch (e: UnknownHostException) {
                println("Unable to connect to server: ${e.message}")
            } catch (e: IOException) {
                println("Error reading/writing to socket: ${e.message}")
            }
        }
    }

     suspend fun getConfigpupBack(configRequest: deviceRequest, socket: Socket,socketViewModel: SocketViewModel) {
        withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val jsonString = gson.toJson(configRequest)
                val outputStream: OutputStream = socket.getOutputStream()
                // Send a message to the server
                val message: String = jsonString
                Timber.d("message = $message")
                outputStream.write(message.toByteArray())
                outputStream.flush()

//                val inputStream: InputStream = socket!!.getInputStream()
//                val buffer = ByteArray(1024)
//                var bytesRead: Int = inputStream.read(buffer)
//                while (bytesRead > 0) {
//                    var response = String(buffer, 0, bytesRead)
//                    val responseJson = JsonParser.parseString(response)
//                    bytesRead = inputStream.read(buffer)
//
//                    if(responseJson.asJsonObject.has("PUBACK"))
//                    {
//                        val puback=JSONObject(response)
//                        homeViewModel.setPuback(puback)
//                    }
//
//                }


            } catch (e: UnknownHostException) {
                println("Unable to connect to server: ${e.message}")
            } catch (e: IOException) {
                println("Error reading/writing to socket: ${e.message}")
            }
        }
    }

    suspend fun getAccessorypupBack(accRequest: accRequest, socket: Socket) {
        withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val jsonString = gson.toJson(accRequest)
                val outputStream: OutputStream = socket.getOutputStream()
                // Send a message to the server
                val message: String = jsonString
                Timber.d("message = $message")
                outputStream.write(message.toByteArray())
                outputStream.flush()
            } catch (e: UnknownHostException) {
                println("Unable to connect to server: ${e.message}")
            } catch (e: IOException) {
                println("Error reading/writing to socket: ${e.message}")
            }
        }
    }

    suspend fun setAccessorypupBack(accRequest: editAccessRequest, socket: Socket) {
        withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val jsonString = gson.toJson(accRequest)
                val outputStream: OutputStream = socket.getOutputStream()
                // Send a message to the server
                val message: String = jsonString
                Timber.d("message = $message")
                outputStream.write(message.toByteArray())
                outputStream.flush()
            } catch (e: UnknownHostException) {
                println("Unable to connect to server: ${e.message}")
            } catch (e: IOException) {
                println("Error reading/writing to socket: ${e.message}")
            }
        }
    }


    suspend fun setConfigurepupBack(configRequest: EditRequest, socket: Socket) {
        withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val jsonString = gson.toJson(configRequest)
                val outputStream: OutputStream = socket.getOutputStream()
                // Send a message to the server
                val message: String = jsonString
                Timber.d("message = $message")
                outputStream.write(message.toByteArray())
                outputStream.flush()
            } catch (e: UnknownHostException) {
                println("Unable to connect to server: ${e.message}")
            } catch (e: IOException) {
                println("Error reading/writing to socket: ${e.message}")
            }
        }
    }

    suspend fun setOtaUpdate(otaRequest: otaRequest, socket: Socket) {
        withContext(Dispatchers.IO) {
            try {
                val gson = Gson()
                val jsonString = gson.toJson(otaRequest)
                val outputStream: OutputStream = socket.getOutputStream()
                // Send a message to the server
                val message: String = jsonString
                Timber.d("message = $message")
                outputStream.write(message.toByteArray())
                outputStream.flush()
            } catch (e: UnknownHostException) {
                println("Unable to connect to server: ${e.message}")
            } catch (e: IOException) {
                println("Error reading/writing to socket: ${e.message}")
            }
        }
    }



}
