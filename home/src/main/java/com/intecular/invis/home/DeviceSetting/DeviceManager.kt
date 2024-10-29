package com.intecular.invis.home.DeviceSetting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.home.home.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceManager(navHostController: NavHostController, socketViewModel: SocketViewModel = hiltViewModel(), sn:String) {
    var showRestart by remember { mutableStateOf(false) }
    var showResetNetwork by remember { mutableStateOf(false) }
    var FactoryDefaults by remember { mutableStateOf(false) }
    var showRestartDevice by remember { mutableStateOf(false) }
    var showResetDevice by remember { mutableStateOf(false) }
    var showTurnonWiFi by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "DeviceManager", color =  MaterialTheme.colorScheme.onSurface)
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
                    containerColor =  MaterialTheme.colorScheme.surfaceBright, // 设置TopAppBar背景色为白色
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
                    .background(MaterialTheme.colorScheme.surfaceVariant),

                verticalArrangement = Arrangement.spacedBy(16.dp)
            )
            {
                DeviceSection(
                    title = "InvisOutlet",
                    options = listOf(
                        "Restart Device",
                        "Reset Network Settings",
                        "Reset to Factory Defaults"
                    ),
                    onOptionClick = listOf(
                        { showRestart =true},
                        { showResetNetwork=true },
                        { FactoryDefaults =true}
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                DeviceSection(
                    title = "InvisDeco",
                    options = listOf(
                        "Restart Device",
                        "Reset Device",
                        "Turn on Wi-Fi Debugger"
                    ),
                    onOptionClick = listOf(
                        { showRestartDevice=true },
                        { showResetDevice=true },
                        { showTurnonWiFi=true}
                    )
                )
            }
        }

        if(showRestart)
        {
            CustomManagerDialog(
                onDismissRequest = { showRestart = false },
                title = "Restart Device",
                text = "Restart InvisOutlet?",
                confirmButton = {
                    TextButton(onClick = {
                        // Perform update action
                        showRestart = false
                    }) {
                        Text("Update",color = MaterialTheme.colorScheme.primary  )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRestart = false }) {
                        Text("Cancel",color = MaterialTheme.colorScheme.primary )
                    }
                }
            )
        }

        if(showResetNetwork){
            CustomManagerDialog(
                onDismissRequest = { showResetNetwork = false },
                title = "Reset Network Settings",
                text = "Are you sure you want to reset InvisOutlet’s network settings?" +
                        "This will clear all Matter configurations and you will need to re-commission your device." +
                        "Note: Device customization settings will not be erased.",
                confirmButton = {
                    TextButton(onClick = {
                        // Perform update action
                        showResetNetwork = false
                    }) {
                        Text("Update",color = MaterialTheme.colorScheme.primary   )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetNetwork = false }) {
                        Text("Cancel",color = MaterialTheme.colorScheme.primary  )
                    }
                }
            )
        }

        if(FactoryDefaults)
        {
            CustomManagerDialog(
                onDismissRequest = { FactoryDefaults = false },
                title = "Reset to Factory Defaults",
                text = "Are you sure you want to reset InvisOutlet to factory defaults?" +
                        "This will clear all Matter configurations and you will need to re-commission your device." +
                        "Note: Device customization settings will also be erased.",
                confirmButton = {
                    TextButton(onClick = {
                        // Perform update action
                        FactoryDefaults = false
                    }) {
                        Text("Update",color = MaterialTheme.colorScheme.primary   )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { FactoryDefaults = false }) {
                        Text("Cancel",color = MaterialTheme.colorScheme.primary  )
                    }
                }
            )
        }


        if(showRestartDevice)
        {
            CustomManagerDialog(
                onDismissRequest = { showRestartDevice = false },
                title = "Reset Device",
                text = "Restart InvisDeco?",
                confirmButton = {
                    TextButton(onClick = {
                        // Perform update action
                        showRestartDevice = false
                    }) {
                        Text("Update",color = MaterialTheme.colorScheme.primary   )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRestartDevice = false }) {
                        Text("Cancel",color = MaterialTheme.colorScheme.primary  )
                    }
                }
            )
        }

        if(showResetDevice)
        {
            CustomManagerDialog(
                onDismissRequest = { showResetDevice = false },
                title = "Restart Device",
                text = "Are you sure you want to reset InvisDeco?" +
                        "This will clear all configuration  settings  and sensor calibration data.",
                confirmButton = {
                    TextButton(onClick = {
                        // Perform update action
                        showResetDevice = false
                    }) {
                        Text("Update",color = MaterialTheme.colorScheme.primary   )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDevice = false }) {
                        Text("Cancel",color = MaterialTheme.colorScheme.primary  )
                    }
                }
            )
        }

        if(showTurnonWiFi)
        {
            CustomManagerDialog(
                onDismissRequest = { showTurnonWiFi = false },
                title = "Turn on Wi-Fi Debugger",
                text = "Are you sure you want to turn on Wi-Fi Debugger on InvisDeco?",
                confirmButton = {
                    TextButton(onClick = {
                        // Perform update action
                        showTurnonWiFi = false
                    }) {
                        Text("Update",color = MaterialTheme.colorScheme.primary   )
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTurnonWiFi = false }) {
                        Text("Cancel",color = MaterialTheme.colorScheme.primary  )
                    }
                }
            )
        }

    }
}


@Composable
fun DeviceSection(title: String, options: List<String>, onOptionClick: List<() -> Unit>) {

    Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceBright, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        itemsIndexed(options) { index, option ->
            ListItem(
                colors = ListItemDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceBright,
                    headlineColor = MaterialTheme.colorScheme.onSurface,
                    leadingIconColor = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionClick[index]() }
                    .padding(vertical = 4.dp, horizontal = 4.dp)
                ,
                headlineContent = {
                    Text(
                        text = option,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                trailingContent = {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_arror),
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant

                    )
                }
            )
        }
    }
}
@Composable
fun CustomManagerDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
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
                .background(MaterialTheme.colorScheme.surfaceBright, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color =  MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = text, color =  MaterialTheme.colorScheme.onSurface)
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


