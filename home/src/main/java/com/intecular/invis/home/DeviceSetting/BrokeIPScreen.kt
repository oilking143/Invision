package com.intecular.invis.home.DeviceSetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.intecular.invis.home.Api.ApiClient
import com.intecular.invis.home.Tcpdatas.Configures.ConfigureResponse
import com.intecular.invis.home.Tcpdatas.EditConfigure.request.AccPrefs
import com.intecular.invis.home.Tcpdatas.EditConfigure.request.EditRequest
import com.intecular.invis.home.Tcpdatas.EditConfigure.request.Mqtt
import com.intecular.invis.home.Tcpdatas.EditConfigure.request.SysPrefs
import com.intecular.invis.home.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrokeIPScreen(navHostController: NavHostController, socketViewModel: SocketViewModel= hiltViewModel(), sn:String) {
    val configResponse by socketViewModel.deviceConfigureLiveData.observeAsState()
    var brokeIp by remember { mutableStateOf(configResponse?.payload?.sys_prefs?.mqtt?.mqtt_broker_url) }
    var enableflag by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var showConfirm by remember { mutableStateOf(false) }
    val editAccresponse by socketViewModel.editaccresponseLiveData.observeAsState()
    var hasShownConfirm by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "MQTT BrokerIP", color = MaterialTheme.colorScheme.onSurface.copy(0.9F))
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
                    containerColor = MaterialTheme.colorScheme.surfaceBright, // 设置TopAppBar背景色为白色
                    titleContentColor = MaterialTheme.colorScheme.onSurface // 设置标题文字颜色为黑色
                )

            )
        }
        , bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    enabled = enableflag,
                    onClick = {
                        coroutineScope.launch {
                            withContext(Dispatchers.Main) {
                                configResponse?.let { brokeIp?.let { it1 ->
                                    setConfigure(socketViewModel,
                                        it1, it)
                                } }
                            }
                        }

                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
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

                brokeIp?.let {
                    CustomOutlinedTextField(
                        value = it,
                        onValueChange = { newText -> brokeIp = newText
                            configResponse?.payload?.sys_prefs?.mqtt?.mqtt_broker_url = newText
                            enableflag=!enableflag},
                        label = "Broker IP",
                        supportingText = "Enter your MQTT broker IP."
                    )
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
}


suspend fun setConfigure( socketViewModel: SocketViewModel,brokeIP:String,
                          configResponse: ConfigureResponse
)
{

    val accPrefs = AccPrefs(configResponse.payload.acc_prefs.outletPwrIndicatorOn,
        configResponse.payload.acc_prefs.pmIndicatorBrightness,
        configResponse.payload.acc_prefs.capacitiveCtrl,
        configResponse.payload.acc_prefs.aqiColorRGBFeature,
        configResponse.payload.acc_prefs.motionAwayFeature,
        configResponse.payload.acc_prefs.adaptiveNightlightFeature,
        configResponse.payload.acc_prefs.adaptiveSensitivity)
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

    val request= EditRequest(packetID=713327,payload)
    socketViewModel.getSocket().let {
        if (it != null) {
            ApiClient().setConfigurepupBack(request, it)
        }
    }

}





