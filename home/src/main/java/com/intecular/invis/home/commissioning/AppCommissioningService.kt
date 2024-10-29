package com.intecular.invis.home.commissioning

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.gms.home.matter.commissioning.CommissioningCompleteMetadata
import com.google.android.gms.home.matter.commissioning.CommissioningRequestMetadata
import com.google.android.gms.home.matter.commissioning.CommissioningService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AppCommissioningService : Service(), CommissioningService.Callback {

    @Inject
    internal lateinit var chipClient: ChipClient

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var commissioningServiceDelegate: CommissioningService

    override fun onCreate() {
        super.onCreate()
        Timber.d("onCreate()")
        commissioningServiceDelegate = CommissioningService.Builder(this).setCallback(this).build()
    }

    override fun onBind(intent: Intent?): IBinder {
        Timber.d("onBind(): intent [${intent}]")
        return commissioningServiceDelegate.asBinder()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand(): intent [${intent}] flags [${flags}] startId [${startId}]")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCommissioningRequested(metadata: CommissioningRequestMetadata) {
        Timber.d(
            "*** onCommissioningRequested ***:\n" +
                    "\tdeviceDescriptor: " +
                    "deviceType [${metadata.deviceDescriptor.deviceType}] " +
                    "vendorId [${metadata.deviceDescriptor.vendorId}] " +
                    "productId [${metadata.deviceDescriptor.productId}]\n" +
                    "\tnetworkLocation: " +
                    "IP address toString() [${metadata.networkLocation.ipAddress}] " +
                    "IP address hostAddress [${metadata.networkLocation.ipAddress.hostAddress}] " +
                    "port [${metadata.networkLocation.port}]\n" +
                    "\tpassCode [${metadata.passcode}]"
        )
        val deviceId = chipClient.deviceId
        serviceScope.launch {
            try {
                Timber.d(
                    "Commissioning: App fabric -> ChipClient.establishPaseConnection(): deviceId [${deviceId}]"
                )
                chipClient.awaitEstablishPaseConnection(
                    deviceId,
                    metadata.networkLocation.ipAddress.hostAddress!!,
                    metadata.networkLocation.port,
                    metadata.passcode
                )

                Timber.d(
                    "Commissioning: App fabric -> ChipClient.commissionDevice(): deviceId [${deviceId}]"
                )
                chipClient.awaitCommissionDevice(deviceId, null)
            } catch (e: Exception) {
                Timber.e(e, "onCommissioningRequested() failed")
                // No way to determine whether this was ATTESTATION_FAILED or DEVICE_UNREACHABLE.
                commissioningServiceDelegate
                    .sendCommissioningError(CommissioningService.CommissioningError.OTHER)
                    .addOnSuccessListener {
                        Timber.d(
                            "Commissioning: commissioningServiceDelegate.sendCommissioningError() succeeded"
                        )
                    }
                    .addOnFailureListener { e2 ->
                        Timber.e(
                            e2,
                            "Commissioning: commissioningServiceDelegate.sendCommissioningError() failed"
                        )
                    }
                return@launch
            }

            Timber.d("Commissioning: Calling commissioningServiceDelegate.sendCommissioningComplete()")
            commissioningServiceDelegate
                .sendCommissioningComplete(
                    CommissioningCompleteMetadata.builder().setToken(deviceId.toString()).build()
                )
                .addOnSuccessListener {
                    Timber.d(
                        "Commissioning: commissioningServiceDelegate.sendCommissioningComplete() succeeded"
                    )
                }
                .addOnFailureListener { e ->
                    Timber.e(
                        e,
                        "Commissioning: commissioningServiceDelegate.sendCommissioningComplete() failed"
                    )
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("onDestroy()")
        serviceJob.cancel()
    }
}
