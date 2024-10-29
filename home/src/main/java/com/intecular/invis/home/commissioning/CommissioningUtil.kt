package com.intecular.invis.home.commissioning

import androidx.activity.result.ActivityResult
import chip.devicecontroller.AttestationInfo
import chip.devicecontroller.DeviceAttestationDelegate
import com.google.android.gms.home.matter.commissioning.CommissioningResult
import com.intecular.invis.base.TaskStatus
import com.intecular.invis.base.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class CommissioningUtil @Inject constructor(
    private val chipClient: ChipClient,
    @ApplicationScope private val applicationScope: CoroutineScope
) {

    private var gpsCommissioningResult: CommissioningResult? = null

    private val _multiadminCommissionDeviceTaskStatus =
        MutableStateFlow<TaskStatus>(TaskStatus.NotStarted)

    val multiadminCommissionDeviceTaskStatus: StateFlow<TaskStatus> =
        _multiadminCommissionDeviceTaskStatus.asStateFlow()

    private var _deviceAttestationFailureIgnored = MutableStateFlow(true)
    val deviceAttestationFailureIgnored: StateFlow<Boolean> =
        _deviceAttestationFailureIgnored.asStateFlow()

    fun gpsCommissioningDeviceSucceeded(activityResult: ActivityResult, deviceId: Long) {
        gpsCommissioningResult = CommissioningResult.fromIntentSenderResult(
            activityResult.resultCode,
            activityResult.data
        )
        Timber.i("Device commissioned successfully! deviceName [${gpsCommissioningResult!!.deviceName}]")
        Timber.i("Device commissioned successfully! room [${gpsCommissioningResult!!.room}]")
        Timber.i(
            "Device commissioned successfully! DeviceDescriptor of device:\n" +
                    "productId [${gpsCommissioningResult!!.commissionedDeviceDescriptor.productId}]\n" +
                    "vendorId [${gpsCommissioningResult!!.commissionedDeviceDescriptor.vendorId}]\n" +
                    "hashCode [${gpsCommissioningResult!!.commissionedDeviceDescriptor.hashCode()}]"
        )
//        getAttributesStatesJsonString(gpsCommissioningResult?.token!!.toLong())
    }

    fun commissionDeviceFailed(resultCode: Int) {
        if (resultCode == 0) {
            // User simply wilfully exited from GPS commissioning.
            return
        }
        val title = "Commissioning the device failed"
        Timber.e(title)
    }

    fun setMultiadminCommissioningTaskStatus(taskStatus: TaskStatus) {
        _multiadminCommissionDeviceTaskStatus.value = taskStatus
    }

    // Device Attestation
    fun setDeviceAttestationDelegate(
        failureTimeoutSeconds: Int = DEVICE_ATTESTATION_FAILED_TIMEOUT_SECONDS
    ) {
        Timber.d("setDeviceAttestationDelegate")
        chipClient.chipDeviceController.setDeviceAttestationDelegate(failureTimeoutSeconds) { devicePtr, _, errorCode ->
            Timber.d(
                "Device attestation errorCode: $errorCode, " +
                        "Look at 'src/credentials/attestation_verifier/DeviceAttestationVerifier.h' " +
                        "AttestationVerificationResult enum to understand the errors"
            )

            if (errorCode == STATUS_PAIRING_SUCCESS) {
                Timber.d("DeviceAttestationDelegate: Success on device attestation.")
                applicationScope.launch {
                    chipClient.chipDeviceController.continueCommissioning(devicePtr, true)
                }
            } else {
                Timber.d("DeviceAttestationDelegate: Error on device attestation [$errorCode].")
                // Ideally, we'd want to show a Dialog and ask the user whether the attestation
                // failure should be ignored or not.
                // Unfortunately, the GPS commissioning API is in control at this point, and the
                // Dialog will only show up after GPS gives us back control.
                // So, we simply ignore the attestation failure for now.
                // TODO: Add a new setting to control that behavior.
                _deviceAttestationFailureIgnored.value = true
                Timber.w("Ignoring attestation failure.")
                applicationScope.launch {
                    chipClient.chipDeviceController.continueCommissioning(devicePtr, true)
                }
            }
        }
    }

    fun resetDeviceAttestationDelegate() {
        Timber.d("resetDeviceAttestationDelegate")
        chipClient.chipDeviceController.setDeviceAttestationDelegate(
            0,
            EmptyAttestationDelegate()
        )
    }

    private class EmptyAttestationDelegate : DeviceAttestationDelegate {
        override fun onDeviceAttestationCompleted(
            devicePtr: Long,
            attestationInfo: AttestationInfo,
            errorCode: Int,
        ) {
        }
    }

    companion object {
        private const val STATUS_PAIRING_SUCCESS = 0

        /** Set for the fail-safe timer before onDeviceAttestationFailed is invoked. */
        private const val DEVICE_ATTESTATION_FAILED_TIMEOUT_SECONDS = 60
    }
}