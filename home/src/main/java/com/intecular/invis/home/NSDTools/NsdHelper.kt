package com.intecular.invis.home.NSDTools

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.intecular.invis.home.home.HomeViewModel
import timber.log.Timber

class NsdHelper(val homeViewModel: HomeViewModel) {
    private val nsdManager: NsdManager = homeViewModel.context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private var discoveryListener = DiscoveryListener()
    private val SERVICE_TYPE = "_invis._tcp."
    companion object {
        private const val TAG = "NSDHelper"
    }

    private inner class DiscoveryListener : NsdManager.DiscoveryListener {
        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Timber.tag(TAG).e("Discovery failed: Error code:%s", errorCode)
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Timber.tag(TAG).e("Stop discovery failed: Error code:%s", errorCode)
            nsdManager.stopServiceDiscovery(this)
        }

        override fun onDiscoveryStarted(serviceType: String) {
            Timber.tag(TAG).d("Service discovery started")
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Timber.tag(TAG).i("Discovery stopped")
        }

        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            Timber.tag(TAG).d("Service discovery success%s", serviceInfo)
            nsdManager.resolveService(serviceInfo, NSDResolveListener(homeViewModel))

        }

        override fun onServiceLost(serviceInfo: NsdServiceInfo) {
            Timber.tag(TAG).e("service lost%s", serviceInfo)
        }
    }

    fun startDiscovery() {
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    fun stopDiscovery() {
        try {
            discoveryListener.let {
                nsdManager.stopServiceDiscovery(it)
            }
        } catch (e: IllegalArgumentException) {
            Timber.tag(TAG).e("Attempted to stop discovery when listener was not registered: %s", e.message)
        }
    }

    fun restartDiscovery() {
        // 停止当前的发现过程
        stopDiscovery()
        // 清除当前的 discoveryListener 并创建新的实例
        discoveryListener = DiscoveryListener()
        // 重新启动服务发现
        startDiscovery()
        Timber.tag(TAG).i("Service discovery restarted")
    }

}