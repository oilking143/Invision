package com.intecular.invis.home.home

import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.SystemClock
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.google.android.gms.home.matter.Matter
import com.google.android.gms.home.matter.commissioning.CommissioningRequest
import com.google.android.gms.home.matter.commissioning.DeviceInfo
import com.google.android.gms.home.matter.commissioning.SharedDeviceData
import com.google.android.gms.home.matter.commissioning.SharedDeviceData.EXTRA_COMMISSIONING_WINDOW_EXPIRATION
import com.google.android.gms.home.matter.commissioning.SharedDeviceData.EXTRA_DEVICE_NAME
import com.google.android.gms.home.matter.commissioning.SharedDeviceData.EXTRA_MANUAL_PAIRING_CODE
import com.google.android.gms.home.matter.commissioning.SharedDeviceData.EXTRA_PRODUCT_ID
import com.google.android.gms.home.matter.commissioning.SharedDeviceData.EXTRA_VENDOR_ID
import com.intecular.invis.base.MIN_COMMISSIONING_WINDOW_EXPIRATION_SECONDS
import com.intecular.invis.base.TaskStatus
import com.intecular.invis.base.ext.getActivity
import com.intecular.invis.base.isMultiAdminCommissioning
import com.intecular.invis.common.ui.resource.theme.surfaceContainerLowLight
import com.intecular.invis.data.data.DeviceRoomData
import com.intecular.invis.data.navigation.Screen
import com.intecular.invis.home.NSDTools.NsdHelper
import com.intecular.invis.home.commissioning.AppCommissioningService
import kotlinx.coroutines.launch
import timber.log.Timber


@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalWearMaterialApi
@ExperimentalMaterial3Api
@Composable
fun HomeScreen(
    navHostController: NavHostController
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val activity = LocalContext.current.getActivity()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val deviceId = remember {
        mutableLongStateOf(0)
    }
    val roomData by homeViewModel.roomDataLiveData.observeAsState(emptyList())
    val updateDeviceId = rememberUpdatedState(newValue = deviceId)


    val commissioningLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            homeViewModel.commissioningUtil.gpsCommissioningDeviceSucceeded(result, updateDeviceId.value.longValue)

        } else {
            homeViewModel.commissioningUtil.commissionDeviceFailed(result.resultCode)
        }
    }
    val multiadminCommissionDeviceTaskStatus by
    homeViewModel.commissioningUtil.multiadminCommissionDeviceTaskStatus.collectAsState()
    val onCommissionDevice: () -> Unit = remember {
        {
            Timber.d("onAddDeviceClick")
            // fixme deviceAttestationFailureIgnored = false
            commissionDevice(activity!!.applicationContext, commissioningLauncher)
        }
    }
    DeviceAttestationDelegate(
        activity,
        multiadminCommissionDeviceTaskStatus,
        homeViewModel,
        commissioningLauncher,
        deviceId
    )

    val bulbLongClicked = remember {
        mutableStateOf((false))
    }
    val openStatus = remember {
        mutableIntStateOf(0)
    }
  val helper = NsdHelper(homeViewModel)
    helper.startDiscovery()

    val showDialog by homeViewModel.showDialog.observeAsState(false)

    ModalNavigationDrawer(
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.clip(RoundedCornerShape(24.dp))) {
                ModalDrawerContent(drawerState, homeViewModel) {
                    navHostController.navigate(Screen.SettingsScreen.route)
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                HomeCenterAlignedTopAppBar(
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                Icons.Rounded.Menu,
                                contentDescription = "MenuButton"
                            )
                        }
                    })
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onCommissionDevice() },
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "AddButton")
                }
            },

            ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.onPrimary)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .padding(10.dp, 0.dp)
                        .fillMaxSize()
                ) {
                    items(roomData) { item: DeviceRoomData ->
                        RoomItem(
                            deviceRoomData = item,
                            homeViewModel = homeViewModel,
                            brightnessClicked = { openStatus ->
                                // 处理亮度点击事件
                                println("Brightness clicked with status: $openStatus")
                                bulbLongClicked.value = true
                            },
                            refreshClick = {
                                helper.restartDiscovery()
                            },
                            settingClick = { index, deviceInfo ->
                                helper.stopDiscovery()
                                if (homeViewModel.getSocket() == null) {
                                    Toast.makeText(activity, "Device Offline!", Toast.LENGTH_LONG).show()
                                } else {
                                    val host =  java.lang.String(homeViewModel.mDnsInfo[index].attributes["hostname"]!!).toString()
                                    navHostController.navigate("device-settings-screen/$host/${item.title}")
                                }
                            }
                        )
                    }
                }
                AdjustBrightnessDialog(bulbLongClicked, openStatus,homeViewModel)
            }


            if(showDialog)
            {
                ConnectionAlert(homeViewModel)
            }

        }
    }
}

fun commissionDevice(
    context: Context,
    commissionDeviceLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
) {
    Timber.d("CommissionDevice: starting")

    val commissionDeviceRequest =
        CommissioningRequest.builder()
            .setCommissioningService(ComponentName(context, AppCommissioningService::class.java))
            .build()

    Matter.getCommissioningClient(context)
        .commissionDevice(commissionDeviceRequest)
        .addOnSuccessListener { result ->
            Timber.d("CommissionDevice: Success getting the IntentSender: result [${result}]")
            commissionDeviceLauncher.launch(IntentSenderRequest.Builder(result).build())
        }
        .addOnFailureListener { error ->
            Timber.e(error)
        }
}

@Composable
private fun DeviceAttestationDelegate(
    activity: ComponentActivity?,
    multiadminCommissionDeviceTaskStatus: TaskStatus,
    homeViewModel: HomeViewModel,
    commissioningLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
    deviceId: MutableState<Long>
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val intent = activity!!.intent
                Timber.d("intent [${intent}]")
                if (isMultiAdminCommissioning(intent)) {
                    Timber.d("Invocation: MultiAdminCommissioning")
                    if (multiadminCommissionDeviceTaskStatus == TaskStatus.NotStarted) {
                        Timber.d("TaskStatus.NotStarted so starting multiadmin commissioning")
                        homeViewModel.commissioningUtil.setMultiadminCommissioningTaskStatus(TaskStatus.InProgress)
                        multiAdminCommissionDevice(
                            activity.applicationContext,
                            intent,
                            homeViewModel,
                            commissioningLauncher,
                        )
                    } else {
                        Timber.d("TaskStatus is *not* NotStarted: $multiadminCommissionDeviceTaskStatus")
                    }
                } else {
                    Timber.d("Invocation: Main")
//                    homeViewModel.subscribeToDevicesPeriodicUpdates()
                }
                // FIXME[TJ]: I had this on fragment's create(). Anything similar to that for composables?
                // We need our own device attestation delegate as we currently only support attestation
                // of test Matter devices. This DeviceAttestationDelegate makes it possible to ignore device
                // attestation failures, which happen if commissioning production devices.
                // TODO: Look into supporting different Root CAs.
                // FIXME: This currently breaks commissioning. Removed for now.
                homeViewModel.commissioningUtil.setDeviceAttestationDelegate()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            homeViewModel.commissioningUtil.resetDeviceAttestationDelegate()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

fun multiAdminCommissionDevice(
    context: Context,
    intent: Intent,
    homeViewModel: HomeViewModel,
    commissionDeviceLauncher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
) {
    Timber.d("CommissionDevice: starting")

    val sharedDeviceData = SharedDeviceData.fromIntent(intent)
    Timber.d("multiadminCommissioning: sharedDeviceData [${sharedDeviceData}]")
    Timber.d("multiadminCommissioning: manualPairingCode [${sharedDeviceData.manualPairingCode}]")

    val commissionRequestBuilder =
        CommissioningRequest.builder()
            .setCommissioningService(ComponentName(context, AppCommissioningService::class.java))

    // Fill in the commissioning request...

    // EXTRA_COMMISSIONING_WINDOW_EXPIRATION is a hint of how much time is remaining in the
    // commissioning window for multi-admin. It is based on the current system uptime.
    // If the user takes too long to select the target commissioning app, then there's not
    // enougj time to complete the multi-admin commissioning and we message it to the user.
    val commissioningWindowExpirationMillis =
        intent.getLongExtra(EXTRA_COMMISSIONING_WINDOW_EXPIRATION, -1L)
    val currentUptimeMillis = SystemClock.elapsedRealtime()
    val timeLeftSeconds = (commissioningWindowExpirationMillis - currentUptimeMillis) / 1000
    Timber.d(
        "commissionDevice: TargetCommissioner for MultiAdmin. " +
                "uptime [${currentUptimeMillis}] " +
                "commissioningWindowExpiration [${commissioningWindowExpirationMillis}] " +
                "-> expires in $timeLeftSeconds seconds"
    )

    if (commissioningWindowExpirationMillis == -1L) {
        Timber.e(
            "EXTRA_COMMISSIONING_WINDOW_EXPIRATION not specified in multi-admin call. " +
                    "Still going ahead with the multi-admin though."
        )
    } else if (timeLeftSeconds < MIN_COMMISSIONING_WINDOW_EXPIRATION_SECONDS) {
        Timber.e(
            "The commissioning window will " +
                    "expire in $timeLeftSeconds seconds, not long enough to complete the commissioning.\n\n" +
                    "In the future, please select the target commissioning application faster to avoid this situation.",
        )
        return
    }

    val deviceName = intent.getStringExtra(EXTRA_DEVICE_NAME)
    commissionRequestBuilder.setDeviceNameHint(deviceName)

    val vendorId = intent.getIntExtra(EXTRA_VENDOR_ID, -1)
    val productId = intent.getIntExtra(EXTRA_PRODUCT_ID, -1)
    val deviceInfo = DeviceInfo.builder().setProductId(productId).setVendorId(vendorId).build()
    commissionRequestBuilder.setDeviceInfo(deviceInfo)

    val manualPairingCode = intent.getStringExtra(EXTRA_MANUAL_PAIRING_CODE)
    commissionRequestBuilder.setOnboardingPayload(manualPairingCode)

    val commissioningRequest = commissionRequestBuilder.build()

    Timber.d(
        "multiadmin: commissioningRequest " +
                "onboardingPayload [${commissioningRequest.onboardingPayload}] " +
                "vendorId [${commissioningRequest.deviceInfo!!.vendorId}] " +
                "productId [${commissioningRequest.deviceInfo!!.productId}]"
    )

    Matter.getCommissioningClient(context)
        .commissionDevice(commissioningRequest)
        .addOnSuccessListener { result ->
            Timber.d("Success getting the IntentSender: result [${result}]")
            commissionDeviceLauncher.launch(IntentSenderRequest.Builder(result).build())
        }
        .addOnFailureListener { error ->
            Timber.e(error)
        }


}

@Composable
fun ModalDrawerContent(
    drawerState: DrawerState,
    homeViewModel: HomeViewModel,
    settingsItemClicked: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Text(
        stringResource(id = com.intecular.invis.common.ui.resource.R.string.home),
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 18.dp)
    )
    homeViewModel.deviceRoomInfo.drawerInfoList.forEach { data ->
        NavigationDrawerItem(
            modifier = Modifier.padding(16.dp, 0.dp),
            label = {
                Text(text = data.deviceRoomName)
            },
            selected = data.deviceRoomName == "",
            onClick = {
                coroutineScope.launch { drawerState.close() }
            },
            icon = {
                Icon(
                    painter = painterResource(id = com.intecular.invis.common.ui.resource.R.drawable.ic_white_empty_home),
                    contentDescription = "Device Room Icon"
                )
            }
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)
    )

    NavigationDrawerItem(
        modifier = Modifier.padding(16.dp, 0.dp),
        label = {
            Text(text = stringResource(id = com.intecular.invis.common.ui.resource.R.string.refresh))
        },
        selected = false,
        onClick = {
            // TODO: 呼叫取得裝置Api
            coroutineScope.launch {
                drawerState.close()
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = com.intecular.invis.common.ui.resource.R.drawable.ic_refresh),
                contentDescription = "Device Room Icon",
            )
        }
    )
    Spacer(modifier = Modifier.width(16.dp)) // Add spacing here
    NavigationDrawerItem(
        modifier = Modifier.padding(16.dp, 0.dp),
        label = {
            Text(text = stringResource(id = com.intecular.invis.common.ui.resource.R.string.settings))
        }, selected = false, onClick = {
            coroutineScope.launch {
                drawerState.close()
                settingsItemClicked()
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = com.intecular.invis.common.ui.resource.R.drawable.ic_settings),
                contentDescription = "Device Room Icon"
            )
        }
    )
}

@ExperimentalComposeUiApi
@ExperimentalWearMaterialApi
@Composable
fun AdjustBrightnessDialog(showDialog: MutableState<Boolean>, openStatus: MutableState<Int>,homeViewModel: HomeViewModel) {
    if (showDialog.value) {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerLow,
                        RoundedCornerShape(15.dp)
                    )
                    .fillMaxWidth()
                    .padding(24.dp, 16.dp, 16.dp, 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Row(horizontalArrangement = Arrangement.Center) {
                        Text(
                            text = stringResource(id = com.intecular.invis.common.ui.resource.R.string.night_light),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.CenterVertically),
//                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            painter = painterResource(id = com.intecular.invis.common.ui.resource.R.drawable.ic_close),
                            contentDescription = "Close brightness dialog",
                            modifier = Modifier
                                .clickable {
                                    showDialog.value = false
                                }
                                .align(Alignment.CenterVertically)
                        )
                    }
                    var sliderProgressValue by rememberSaveable { mutableIntStateOf(if (openStatus.value == 1) 50 else 0) }
                    val state = rememberComposeVerticalSlider()
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(0.dp, 12.dp)
                    ) {
                        VerticalSlider(
                            state = state,
                            onProgressChanged = { sliderProgressValue = it },
                            onStopTrackingTouch = {
                                sliderProgressValue = it
                                homeViewModel.setnightLightStatus(1,sliderProgressValue)

                            },
                            enabled = state.isEnabled.value,
                            progressValue = sliderProgressValue
                        )
                        Text(
                            text = "$sliderProgressValue%",
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(0.dp, 10.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Icon(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(0.dp, 10.dp),
                            painter = painterResource(id = com.intecular.invis.common.ui.resource.R.drawable.ic_progress_light_bulb),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = "Progress light bulb icon"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectionAlert(homeViewModel: HomeViewModel) {
    var openDialog by remember { mutableStateOf(true) }

    if (openDialog) {
        AlertDialog(
            onDismissRequest = {

                openDialog = false
                homeViewModel.onDialogDismiss()
            },
            text = {
                Text("Unable connect to Device. Please Reconnect Wifi and Try Again")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog = false
                        homeViewModel.onDialogDismiss()
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}