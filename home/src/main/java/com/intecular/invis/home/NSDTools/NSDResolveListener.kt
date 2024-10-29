package com.intecular.invis.home.NSDTools

import android.annotation.SuppressLint
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import com.intecular.invis.home.home.HomeViewModel
import timber.log.Timber


class NSDResolveListener(val homeViewModel: HomeViewModel): NsdManager.ResolveListener {
    override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
        Timber.tag(homeViewModel.TAG).e("Resolve failed%s", errorCode)
    }

    @SuppressLint("TimberArgCount", "NewApi")
    override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
        Timber.tag(homeViewModel.TAG).d("Resolve Succeeded. %s", serviceInfo)
        Timber.tag(homeViewModel.TAG).d("Resolve Succeeded. Hostname from TXTRecord: %s",
            java.lang.String(serviceInfo!!.attributes["hostname"]!!)
        )
        homeViewModel.setDnsInfo(serviceInfo)

    }
}