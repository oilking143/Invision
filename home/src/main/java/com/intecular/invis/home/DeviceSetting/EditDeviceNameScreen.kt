package com.intecular.invis.home.DeviceSetting

import android.content.Context
import android.content.SharedPreferences
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.home.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDeviceNameScreen(navHostController: NavHostController, socketViewModel:SocketViewModel = hiltViewModel(),sn:String) {

    val infoResponse by socketViewModel.deviceInfoLiveData.observeAsState()
    var deviceName by remember { mutableStateOf("") }
    var enableFlag by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(infoResponse) {

        if(getSavedText(sharedPreferences).isEmpty()){
            if (infoResponse != null) {
                deviceName = infoResponse!!.payload.callbackArgs.IM.MAC
            } else {
                socketViewModel.getDevices()
            }
        }else{
            deviceName = getSavedText(sharedPreferences)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Edit Device Name", color = MaterialTheme.colorScheme.onSurface)
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
                    enabled = enableFlag,
                    onClick = {
                        coroutineScope.launch {
                            withContext(Dispatchers. Main) {
                                saveText(sharedPreferences,deviceName)
                            }

                        }

                    },
                    modifier = Modifier.fillMaxWidth().padding(48.dp),
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
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                ,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomOutlinedTextField(
                    value = deviceName,
                    onValueChange = { newText -> deviceName = newText
                        if(newText.isNotEmpty() && deviceName!=infoResponse!!.payload.callbackArgs.IM.MAC){
                            enableFlag=true
                        }else{
                            enableFlag=false
                        }
                                    },
                    label = "Device Name",
                    supportingText = "Enter a new name to change your device name."
                )
            }
        }

    }
}


fun saveText(sharedPreferences: SharedPreferences, text: String) {
    sharedPreferences.edit().putString("saved_text", text).apply()
}

fun getSavedText(sharedPreferences: SharedPreferences): String {
    return sharedPreferences.getString("saved_text", "") ?: ""
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    supportingText: String,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
            )
        },
        supportingText = {
            Text(
                text = supportingText,
            )
        },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_close),
                        contentDescription = "Clear"
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
