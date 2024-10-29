package com.intecular.invis.home.DeviceSetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.intecular.invis.home.Api.ApiClient
import com.intecular.invis.home.Tcpdatas.OTA.request.Payload
import com.intecular.invis.home.Tcpdatas.OTA.request.otaRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(navHostController: NavHostController,socketViewModel: SocketViewModel = hiltViewModel(), sn: String,select:String) {
    val updateResponse by socketViewModel.deviceUpdateLiveData.observeAsState()
    var loading by remember { mutableStateOf(false) }
    var finish by remember { mutableStateOf(false) }
    var DialogDefaults by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(select) {

        if(select=="InvisOutlet"){
            updateDevice(socketViewModel,1)
        }else{
            updateDevice(socketViewModel,2)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Software Update", color = MaterialTheme.colorScheme.onSurface.copy(0.9F))
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
        ,
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

                if (updateResponse?.sn=="fail000"){
                    CustomManagerDialog(
                        onDismissRequest = { DialogDefaults = false },
                        title = "Update Fail",
                        text = "InvisOutlet has been updated Failed. "+
                                "Please check if it is the latest version.",
                        confirmButton = {
                            TextButton(onClick = {
                                // Perform update action
                                DialogDefaults = false
                            }) {
                                Text("Update",color = MaterialTheme.colorScheme.primary   )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { DialogDefaults = false }) {
                                Text("Cancel",color = MaterialTheme.colorScheme.primary  )
                            }
                        }
                    )
                }

                if(updateResponse!=null)
                    loading = true
                else if (updateResponse?.payload?.callbackArgs?.get(1) == 100){
                    loading=false
                    finish=true
                }


                if (loading) {
                    Text(text = "Updating InvisOutlet...", style = MaterialTheme.typography.titleMedium, color =  MaterialTheme.colorScheme.onSurface)
                    LinearProgressIndicator(
                        progress = { updateResponse?.payload?.callbackArgs?.get(1)?.toFloat()!! },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                if(updateResponse==null){
                    CustomManagerDialog(
                        onDismissRequest = { },
                        title = "Update Fail",
                        text = "InvisOutlet has been updated fail. "+
                                "Please check if it is the latest version",
                        confirmButton = {},
                        dismissButton = {
                            TextButton(onClick = {  navHostController.popBackStack() }) {
                                Text("Cancel",color = MaterialTheme.colorScheme.primary  )
                            }
                        }
                    )
                }

                if(finish)
                {
                    CustomManagerDialog(
                        onDismissRequest = { DialogDefaults = false },
                        title = "Update Success",
                        text = "InvisOutlet has been updated successfully. "+
                                "The device will blink and automatically reboot in a few seconds.",
                        confirmButton = {
                            TextButton(onClick = {
                                // Perform update action
                                DialogDefaults = false
                            }) {
                                Text("Update",color = MaterialTheme.colorScheme.primary   )
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { DialogDefaults = false }) {
                                Text("Cancel",color = MaterialTheme.colorScheme.primary  )
                            }
                        }
                    )
                }



            }
        }

    }
}


    suspend fun updateDevice(socketViewModel: SocketViewModel,callBack:Int){
        val callbackArgs = callBack
        val playoad= Payload(callbackArgs =listOf(callbackArgs) , callbackName = 21)
        val request= otaRequest(packetID=414997,payload=playoad)
        socketViewModel.getSocket()?.let { ApiClient().setOtaUpdate(request, it) }
    }

