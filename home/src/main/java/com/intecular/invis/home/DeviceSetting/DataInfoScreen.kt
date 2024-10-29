package com.intecular.invis.home.DeviceSetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.intecular.invis.home.home.HomeViewModel
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataInfoScreen(navHostController: NavHostController,socketViewModel: SocketViewModel, sn:String) {

    val infoResponse by socketViewModel.deviceInfoLiveData.observeAsState()
    LaunchedEffect(infoResponse) {
        if (infoResponse == null) {
            socketViewModel.getDevices()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Device Information", color =  MaterialTheme.colorScheme.onSurface)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "BackButton",
                            tint =  MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceBright, // 设置TopAppBar背景色为白色
                    titleContentColor =  MaterialTheme.colorScheme.surface // 设置标题文字颜色为黑色
                )

            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background( MaterialTheme.colorScheme.surfaceVariant)
        )
        {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                ,

                verticalArrangement = Arrangement.spacedBy(16.dp)
            )
            {
                Column(modifier = Modifier.padding(16.dp)) {
                    infoResponse?.payload?.callbackArgs?.IM?.fw_rev?.let {
                        infoResponse?.payload?.callbackArgs?.IM?.sn?.let { it1 ->
                            infoResponse?.payload?.callbackArgs?.IM?.MAC?.let { it2 ->
                                DeviceInformation(
                                    title = "InvisOutlet",
                                    serialNumber = it1,
                                    macAddress = it2,
                                    firmwareVersion = it,
                                    isOnline = false
                                )
                            }
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    infoResponse?.payload?.callbackArgs?.PM?.fw_rev?.let {
                        infoResponse?.payload?.callbackArgs?.PM?.sn?.let { it1 ->
                            infoResponse?.payload?.callbackArgs?.PM?.MAC?.let { it2 ->
                                infoResponse?.payload?.callbackArgs?.PM?.online?.let { it3 ->
                                    DeviceInformation(
                                        title = "InvisDeco",
                                        serialNumber = it1,
                                        macAddress = it2,
                                        firmwareVersion = it,
                                        isOnline = it3
                                    )
                                }
                            }
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                }
            }
        }

    }
}


@Composable
fun DeviceInformation(
    title: String,
    serialNumber: String,
    macAddress: String,
    firmwareVersion: String,
    isOnline: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(4.dp))

        Text(text = title, style = MaterialTheme.typography.titleMedium, color =  MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        if (isOnline) {
            Text(text = "Online", color =  MaterialTheme.colorScheme.onSurface, fontSize = 16.sp,style = MaterialTheme.typography.bodyMedium)
            Text(text = "YES", color =  MaterialTheme.colorScheme.onSurface, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
        }
        Text(text = "Serial number",color =  MaterialTheme.colorScheme.onSurface,style = MaterialTheme.typography.bodyMedium)
        Text(text = serialNumber, fontSize = 16.sp,color =  MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "MAC address",color =  MaterialTheme.colorScheme.onSurface,style = MaterialTheme.typography.bodyMedium)
        Text(text = macAddress, fontSize = 16.sp,color =  MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Firmware version",color =  MaterialTheme.colorScheme.onSurface,style = MaterialTheme.typography.bodyMedium)
        Text(text = firmwareVersion, fontSize = 16.sp,color =  MaterialTheme.colorScheme.onSurface)
    }
}


