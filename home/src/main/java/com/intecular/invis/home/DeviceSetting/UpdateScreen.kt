package com.intecular.invis.home.DeviceSetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.intecular.invis.home.Api.ApiClient
import com.intecular.invis.home.Tcpdatas.OTA.request.Payload
import com.intecular.invis.home.Tcpdatas.OTA.request.otaRequest
import com.intecular.invis.home.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(navHostController: NavHostController, socketViewModel: SocketViewModel = hiltViewModel(), sn:String) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedDevice by remember { mutableStateOf("InvisOutlet") }
    val infoResponse by socketViewModel.deviceInfoLiveData.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    //syncData
    LaunchedEffect(infoResponse) {
        if (infoResponse == null) {
            socketViewModel.getDevices()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Software Update",  color = MaterialTheme.colorScheme.onSurface )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "BackButton",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor =  MaterialTheme.colorScheme.surfaceBright,
                    titleContentColor =  MaterialTheme.colorScheme.onSurface
                ),
            )
        }
        , bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        showDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // 使用 drawable 中的颜色
                        contentColor = MaterialTheme.colorScheme.primary// 使用 drawable 中的颜色
                    )
                ) {
                    Text(text = "Perform Software Update",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            if (showDialog) {
                CustomAlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = "Software Update",
                    text = "Select a device to perform software update.",
                    confirmButton = {
                        TextButton(onClick = {
                            // Perform update action
                            showDialog = false
                            coroutineScope.launch {
                                withContext(Dispatchers.Main) {

                                    navHostController.navigate("update_progress/$sn/$selectedDevice")
                                }
                            }

                        }) {
                            Text("Update",color =  MaterialTheme.colorScheme.primary )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Cancel", color = MaterialTheme.colorScheme.primary)
                        }
                    },
                    selectedDevice = selectedDevice,
                    onDeviceSelected = { selectedDevice = it }
                )
            }


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

                Spacer(modifier = Modifier.height(16.dp))
                infoResponse?.payload?.callbackArgs?.IM?.fw_rev?.let {
                    DeviceUpdateInformation(
                        title = "InvisOutlet",
                        currentVersion = it,
                        availableVersion = it,
                        webCurrentVersion = it,
                        webAvailableVersion = it
                    )
                }
                Spacer(modifier = Modifier.padding(vertical = 16.dp))
                infoResponse?.payload?.callbackArgs?.PM?.fw_rev?.let {
                    DeviceUpdateInformation(
                        title = "InvisOutlet",
                        currentVersion = it,
                        availableVersion = it,
                        webCurrentVersion = it,
                        webAvailableVersion = it
                    )
                }
            }
        }

    }
}



@Composable
fun DeviceUpdateInformation(
    title: String,
    currentVersion: String,
    availableVersion: String,
    webCurrentVersion: String?,
    webAvailableVersion: String?
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, style = MaterialTheme.typography.titleMedium, color =  MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Text(text = "Current version", color =  MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
        Text(text = currentVersion, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Text(text = "Available version", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
        Text(text = availableVersion, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        if (webCurrentVersion != null && webAvailableVersion != null) {
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Text(text = "Web current version", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
            Text(text = webCurrentVersion, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.padding(vertical = 4.dp))
            Text(text = "Web available version", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
            Text(text = webAvailableVersion, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    selectedDevice: String,
    onDeviceSelected: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7F), shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = text, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))
            listOf("InvisOutlet", "InvisDeco").forEachIndexed { index, device ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (device == selectedDevice),
                            onClick = { onDeviceSelected(device) }
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (device == selectedDevice),
                        onClick = { onDeviceSelected(device) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary// Light Blue color
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = device, color = MaterialTheme.colorScheme.onSurface)
                }
                if (index < 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                dismissButton()
                Spacer(modifier = Modifier.width(8.dp))
                confirmButton()
            }
        }

    }

}
