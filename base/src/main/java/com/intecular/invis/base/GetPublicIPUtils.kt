package com.intecular.invis.base

import android.content.Context
import android.net.wifi.WifiManager
import androidx.core.content.ContentProviderCompat.requireContext
import com.intecular.invis.base.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.URL
import javax.inject.Inject


class GetPublicIPUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcherIo: CoroutineDispatcher
) {

   suspend fun getPublicIp() = withContext(dispatcherIo) {
       var ip = ""
       try {
           val en = NetworkInterface.getNetworkInterfaces()
           while (en.hasMoreElements()) {
               val enumIpAddress = en.nextElement().inetAddresses
               while (enumIpAddress.hasMoreElements()) {
                   val inetAddress = enumIpAddress.nextElement()
                   if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                       ip = inetAddress.hostAddress?.toString() ?: ""
                   }
               }
           }
       } catch (e: Exception) {
           e.printStackTrace()
       }
       ip
    }
}