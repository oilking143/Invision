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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.home.Api.ApiClient
import com.intecular.invis.home.Tcpdatas.Configures.ConfigureResponse
import com.intecular.invis.home.Tcpdatas.EditConfigure.request.AccPrefs
import com.intecular.invis.home.Tcpdatas.EditConfigure.request.EditRequest
import com.intecular.invis.home.Tcpdatas.EditConfigure.request.Mqtt
import com.intecular.invis.home.Tcpdatas.EditConfigure.request.SysPrefs
import com.intecular.invis.home.home.Tcpdatas.deviceConfig.Response.Request.Payload
import com.intecular.invis.home.home.Tcpdatas.deviceConfig.Response.Request.deviceRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizationsScreen(navHostController: NavHostController, socketViewModel: SocketViewModel
                         ,sn:String) {



    val configResponse by socketViewModel.deviceConfigureLiveData.observeAsState()

    var outletPwrIndicatorOn by remember { mutableStateOf(0) }
    var pmIndicatorBrightness by remember { mutableStateOf(0.5F) }
    var capacitiveCtrl by remember { mutableStateOf(1) }
    var aqiColorRGBFeature by remember { mutableStateOf(1) }
    var motionAwayFeature by remember { mutableStateOf(0) }
    var adaptiveNightlightFeature by remember { mutableStateOf(0) }
    var adaptiveSensitivity by remember { mutableStateOf(0.5F) }
    var showConfirm by remember { mutableStateOf(false) }
    val editAccresponse by socketViewModel.editaccresponseLiveData.observeAsState()
    var hasShownConfirm by remember { mutableStateOf(false) }
    //不一定有值，要做null防護
    if(configResponse==null){
        LaunchedEffect(Unit) {
            val payload = Payload(callbackName = 1)
            val request = deviceRequest(packetID = 819796, payload = payload)
            socketViewModel.getSocket().let {
                if (it != null) {
                    ApiClient().getConfigpupBack(request, it,socketViewModel)
                }
            }
        }
    }
    else{
        outletPwrIndicatorOn = configResponse?.payload?.acc_prefs?.outletPwrIndicatorOn!!
        pmIndicatorBrightness =
            configResponse?.payload?.acc_prefs?.pmIndicatorBrightness!!.toFloat()/100
        capacitiveCtrl = configResponse?.payload?.acc_prefs?.capacitiveCtrl!!
        aqiColorRGBFeature = configResponse?.payload?.acc_prefs?.aqiColorRGBFeature!!
        motionAwayFeature = configResponse?.payload?.acc_prefs?.motionAwayFeature!!
        adaptiveNightlightFeature = configResponse?.payload?.acc_prefs?.adaptiveNightlightFeature!!
        adaptiveSensitivity = configResponse?.payload?.acc_prefs?.adaptiveSensitivity!!.toFloat()/10
    }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Customizations", color = MaterialTheme.colorScheme.onSurface)
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
                    containerColor = MaterialTheme.colorScheme.surfaceBright,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )

            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Button(
                    onClick = {

                        coroutineScope.launch {
                            withContext(Dispatchers.Main) {
                                configResponse?.let {
                                    setConfigure(socketViewModel,
                                        outletPwrIndicatorOn,
                                        pmIndicatorBrightness,
                                        capacitiveCtrl,
                                        aqiColorRGBFeature,
                                        motionAwayFeature,
                                        adaptiveNightlightFeature,
                                        adaptiveSensitivity,
                                        it
                                    )
                                }
                            }
                            showConfirm = true
                        }



                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            bottom = 48.dp, start = 48.dp, end = 48.dp
                        ),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // 使用 drawable 中的颜色
                        contentColor = MaterialTheme.colorScheme.primary

                    )) {
                    Text(text = "Save",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ){
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    )

                ,
                verticalArrangement = Arrangement.spacedBy(16.dp),

                )
            {

                item {
                    Text(text = "Device Preferences",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodySmall
                    )
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright,
                            headlineColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        headlineContent = {
                            Text(
                                text = "Outlet Indicator",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_setting_outlet),
                                contentDescription = "Outlet Indicator",
                            )
                        },
                        trailingContent = {
                            var checked by remember {
                                if(outletPwrIndicatorOn==1){
                                    mutableStateOf(true)
                                }else{
                                    mutableStateOf(false)
                                }

                            }

                            Switch(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                    outletPwrIndicatorOn = if (it) 1 else 0
                                },
                                thumbContent = if (checked) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }

                                }
                            )
                        }
                    )
                    HorizontalDivider()

                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright,
                            headlineColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.onSurface,
                            trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        headlineContent = {
                            Text(
                                text = "Indicator Brightness",
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.backlight_high),
                                contentDescription = "Indicator Brightness",
                            )
                        },
                        trailingContent = {
                            Text(
                                text = "${(pmIndicatorBrightness*100).toInt()}%",
                            )
                        }
                    )
                    HorizontalDivider()

                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright,
                            headlineColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.onSurface,
                            trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        headlineContent = {
                            Column {
                                Slider(
                                    value = pmIndicatorBrightness,
                                    onValueChange = { pmIndicatorBrightness = it }
                                )
                            }
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.brightness_empty),
                                contentDescription = "Empty Brightness",
                            )
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.brightness_high),
                                contentDescription = "Max Brightness",
                            )
                        }
                    )
                    HorizontalDivider()

                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright,
                            headlineColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        headlineContent = {
                            Text(
                                text ="Touch Control",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.touch_app),
                                contentDescription = "Outlet Indicator",
                            )
                        },
                        trailingContent = {
                            var checked by remember {
                                if(capacitiveCtrl==1){
                                    mutableStateOf(true)
                                }else{
                                    mutableStateOf(false)
                                }

                            }

                            Switch(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                    capacitiveCtrl = if (it) 1 else 0
                                },
                                thumbContent = if (checked) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }

                                }
                            )
                        }
                    )
                    HorizontalDivider()

                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright,
                            headlineColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        headlineContent = {
                            Text(
                                text ="AQI RGB Color Code",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.aeading_element),
                                contentDescription = "Outlet Indicator",
                            )
                        },
                        trailingContent = {
                            var checked by remember {
                                if(aqiColorRGBFeature==1){
                                    mutableStateOf(true)
                                }else{
                                    mutableStateOf(false)
                                }

                            }

                            Switch(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                    aqiColorRGBFeature = if (it) 1 else 0
                                },
                                thumbContent = if (checked) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }

                                }
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright,
                            headlineColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        headlineContent = {
                            Text(
                                text ="Home Security",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.house_with_shield),
                                contentDescription = "Outlet Indicator",
                            )
                        },
                        trailingContent = {
                            var checked by remember {
                                if(motionAwayFeature==1){
                                    mutableStateOf(true)
                                }else{
                                    mutableStateOf(false)
                                }

                            }

                            Switch(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                    motionAwayFeature = if (it) 1 else 0
                                },
                                thumbContent = if (checked) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }

                                }
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright,
                            headlineColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        headlineContent = {
                            Text(
                                text ="Adaptive Nightlight",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.night_sight_auto),
                                contentDescription = "Outlet Indicator",
                            )
                        },
                        trailingContent = {
                            var checked by remember {
                                if(adaptiveNightlightFeature==1){
                                    mutableStateOf(true)
                                }else{
                                    mutableStateOf(false)
                                }

                            }

                            Switch(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                    adaptiveNightlightFeature = if (it) 1 else 0
                                },
                                thumbContent = if (checked) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }
                                } else {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Close,
                                            contentDescription = null,
                                            modifier = Modifier.size(SwitchDefaults.IconSize),
                                        )
                                    }

                                }
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright,
                            headlineColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        headlineContent = {
                            Text(
                                text ="Adaptive Sensitivity",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.sensors_krx),
                                contentDescription = "Outlet Indicator",
                            )
                        },
                        trailingContent = {

                            Text(
                                text = "level ${(adaptiveSensitivity*10).toInt()}",
                            )

                        }
                    )
                    HorizontalDivider()

                    HorizontalDivider()
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright,
                            headlineColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.onSurface,
                            trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        headlineContent = {

                            Column {
                                Slider(
                                    value = adaptiveSensitivity,
                                    onValueChange = { adaptiveSensitivity = it }
                                )
                            }
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.brightness_empty),
                                contentDescription = "Empty Brightness",
                            )
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.brightness_high),
                                contentDescription = "Max Brightness",
                            )
                        }

                    )
                    Text(text = "Customize the device features and settings as you prefer.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                item {

                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright,
                            headlineColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        headlineContent = {
                            Text(
                                text ="MQTT",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.hub),
                                contentDescription = "Outlet Indicator",
                            )
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_arror),
                                contentDescription = "Navigate",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )

                        },
                        modifier = Modifier.clickable {

                            navHostController.navigate("mqtt_screen/$sn")
                        }
                    )
                    Text(text = "These are the settings for advanced users. Please configure with care.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )

                }
            }
        }


    }


    if(editAccresponse?.PUBACK == 1 && !hasShownConfirm){
        showConfirm=true
        hasShownConfirm=true
    }

    if (showConfirm){
        CustomConfirmDialog(
            onDismissRequest = { showConfirm = false },
            title = "Configure Success",
            dismissButton = {
                TextButton(onClick = {
                    showConfirm = false
                    editAccresponse?.PUBACK = 0
                    hasShownConfirm = false
                }) {
                    Text("OK",color = MaterialTheme.colorScheme.primary )
                }
            }
        )
    }


}

suspend fun setConfigure( socketViewModel: SocketViewModel,outletPwrIndicatorOn:Int,
                          pmIndicatorBrightness:Float,
                          capacitiveCtrl:Int,
                          aqiColorRGBFeature:Int,
                          motionAwayFeature:Int,
                          adaptiveNightlightFeature:Int,
                          adaptiveSensitivity:Float,
                          configResponse:ConfigureResponse)
{

    val accPrefs = AccPrefs(outletPwrIndicatorOn,
        (pmIndicatorBrightness*100).toInt(),
        capacitiveCtrl,
        aqiColorRGBFeature,
        motionAwayFeature,
        adaptiveNightlightFeature,
        (adaptiveSensitivity*10).toInt())
    val mqtt= configResponse.payload.sys_prefs.mqtt.enabled.let {
        configResponse.payload.sys_prefs.mqtt.mqtt_broker_url.let { it1 ->
            configResponse.payload.sys_prefs.mqtt.user.let { it2 ->
                configResponse.payload.sys_prefs.mqtt.pass.let { it3 ->
                    Mqtt(
                        it,
                        it1,
                        it2,
                        it3
                    )
                }
            }
        }
    }
    val sys_prefs = mqtt.let { SysPrefs(mqtt = it) }

    val callbackArg = sys_prefs.let {
        com.intecular.invis.home.Tcpdatas.EditConfigure.request.CallbackArg(
            acc_prefs =accPrefs , sys_prefs = it
        )
    }
    val payload = com.intecular.invis.home.Tcpdatas.EditConfigure.request.Payload(
        callbackName = 2
        , callbackArgs = listOf(callbackArg))

    val request=EditRequest(packetID=8989889,payload)
    socketViewModel.getSocket().let {
        if (it != null) {
            ApiClient().setConfigurepupBack(request, it)
        }
    }


    //送出後要順便renewData
    configResponse.payload.acc_prefs.outletPwrIndicatorOn = outletPwrIndicatorOn
    configResponse.payload.acc_prefs.pmIndicatorBrightness = (pmIndicatorBrightness*100).toInt()
    configResponse.payload.acc_prefs.capacitiveCtrl = capacitiveCtrl
    configResponse.payload.acc_prefs.aqiColorRGBFeature=aqiColorRGBFeature
    configResponse.payload.acc_prefs.motionAwayFeature = motionAwayFeature
    configResponse.payload.acc_prefs. adaptiveNightlightFeature = adaptiveNightlightFeature
    configResponse.payload.acc_prefs. adaptiveSensitivity = (adaptiveSensitivity*10).toInt()

}

@Composable
fun CustomConfirmDialog(
    onDismissRequest: () -> Unit,
    title: String,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                dismissButton()
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

    }
}



