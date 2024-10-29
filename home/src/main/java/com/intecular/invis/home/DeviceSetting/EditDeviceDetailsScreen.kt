package com.intecular.invis.home.DeviceSetting


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.home.Api.ApiClient
import com.intecular.invis.home.Tcpdatas.Accessory.request.Payload
import com.intecular.invis.home.Tcpdatas.Accessory.request.accRequest
import com.intecular.invis.home.Tcpdatas.EditAccessory.request.CallbackArg
import com.intecular.invis.home.Tcpdatas.EditAccessory.request.EditPayload
import com.intecular.invis.home.Tcpdatas.EditAccessory.request.editAccessRequest
import com.intecular.invis.home.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeviceDetailsScreen(
    navHostController: NavHostController,
    socketViewModel: SocketViewModel= hiltViewModel(),
    sn:String
) {
    val accresponse by socketViewModel.accresponseLiveData.observeAsState()
    val editAccresponse by socketViewModel.editaccresponseLiveData.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    var showConfirm by remember { mutableStateOf(false) }
    var hasShownConfirm by remember { mutableStateOf(false) }


    var outlet1 by remember { mutableStateOf("") }
    var outlet2 by remember { mutableStateOf("") }
    var nightlight by remember { mutableStateOf("") }
    //因為不一定會有資料回來，所以要做null check
    if(accresponse!=null){
        outlet1 = "  ${accresponse?.payload?.get(0)?.name}"
        outlet2= "  ${accresponse?.payload?.get(1)?.name}"
        nightlight = "  ${accresponse?.payload?.get(2)?.name}"
    }

    var enableFlag by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        getInfo(socketViewModel)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Edit Device Details", color = MaterialTheme.colorScheme.onSurface) // 设置标题文字为黑色
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "BackButton",
                            tint = MaterialTheme.colorScheme.onSurface // 设置图标颜色为黑色
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceBright, // 设置TopAppBar背景色为白色
                    titleContentColor = MaterialTheme.colorScheme.onSurface // 设置标题文字颜色为黑色
                )
            )

        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Button(
                    enabled = enableFlag,
                    onClick = {
                        coroutineScope.launch {
                            withContext(Dispatchers. Main) {
                                setInfo(socketViewModel,outlet1,outlet2,nightlight)
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
        ){
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                CustomTextFieldDetail(label = "Outlet 1", value = outlet1,R.drawable.ic_setting_outlet, onValueChange = { outlet1 = it
                    enableFlag = if(outlet1.isNotEmpty() && accresponse?.payload?.get(0)?.name!=outlet1){
                        true
                    }else{
                        false
                    }
                })
                CustomTextFieldDetail(label = "Outlet 2", value = outlet2,R.drawable.ic_setting_outlet, onValueChange = { outlet2 = it
                    enableFlag = if(outlet2.isNotEmpty() && accresponse?.payload?.get(1)?.name!=outlet1){
                        true
                    }else{
                        false
                    }
                })
                CustomTextFieldDetail(label = "Nightlight", value = nightlight,R.drawable.icon_setting_bright, onValueChange = { nightlight = it
                    enableFlag = if(nightlight.isNotEmpty() && accresponse?.payload?.get(2)?.name!=nightlight){
                        true
                    }else{
                        false
                    }
                })
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
                    refreshCurrentScreen(navHostController)
                }) {
                    Text("OK",color = MaterialTheme.colorScheme.primary )
                }
            }
        )
    }
}


suspend fun getInfo( socketViewModel: SocketViewModel)
{
    val payload = Payload(callbackName = 3)
    val request = accRequest(packetID = 180072, payload = payload)
    socketViewModel.getSocket()?.let { ApiClient().getAccessorypupBack(request, it) }
}

suspend fun setInfo( socketViewModel: SocketViewModel,outlet1:String,outlet2: String,nughtlight:String)
{
    val callbackArg = mutableListOf(
        CallbackArg(1,outlet1)
        ,CallbackArg(2,outlet2)
        ,CallbackArg(3,nughtlight))
    val payload = EditPayload(callbackName = 4, callbackArgs = callbackArg)
    val request = editAccessRequest(packetID = 950302, payload)
    socketViewModel.getSocket()?.let { ApiClient().setAccessorypupBack(request, it) }
}

fun refreshCurrentScreen(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    currentRoute?.let {
        navController.navigate(it) {
            popUpTo(it) { inclusive = true }
        }
    }
}

@Composable
fun CustomTextFieldDetail(label: String, value: String,id:Int, onValueChange: (String) -> Unit) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(painter = painterResource(id = R.drawable.icon_close), contentDescription = "Clear")
                }
            }
        },
        label = {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall
            ) },
        leadingIcon = {
            Icon( painterResource(id = id), modifier =Modifier .size(22.dp, 22.dp) , contentDescription = null)
        },
        modifier = Modifier.fillMaxWidth()
    )

}

