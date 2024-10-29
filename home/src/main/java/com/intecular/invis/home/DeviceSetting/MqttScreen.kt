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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.home.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MqttScreen(navHostController: NavHostController, socketViewModel: SocketViewModel,sn:String) {
    val configResponse by socketViewModel.deviceConfigureLiveData.observeAsState()

    var enabled by remember { mutableStateOf(0) }
    var mqtt_broker_url by remember { mutableStateOf("mqtt://192.168.1.26:1883") }
    var user by remember { mutableStateOf("mqttuser") }
    var pass by remember { mutableStateOf("mqttpass") }

    if(configResponse!=null){
        enabled = configResponse?.payload?.sys_prefs?.mqtt?.enabled!!
        mqtt_broker_url = configResponse?.payload?.sys_prefs?.mqtt?.mqtt_broker_url!!
        user = configResponse?.payload?.sys_prefs?.mqtt?.user!!
        pass = configResponse?.payload?.sys_prefs?.mqtt?.pass!!
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "MQTT", color = MaterialTheme.colorScheme.onSurface)
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
                    containerColor = MaterialTheme.colorScheme.surfaceBright,// 设置TopAppBar背景色为白色
                    titleContentColor = MaterialTheme.colorScheme.surface // 设置标题文字颜色为黑色
                )

            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surfaceBright,
                        shape = RoundedCornerShape(16.dp) )
                    .padding(16.dp),

                verticalArrangement = Arrangement.spacedBy(16.dp)
            )
            {
                ListItem(
                    colors = ListItemDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.surfaceBright,
                        headlineColor = MaterialTheme.colorScheme.onSurface,
                        leadingIconColor = MaterialTheme.colorScheme.onSurface,
                        trailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    headlineContent = {
                        Text(
                            text = "MQTT",
                        )
                    },
                    trailingContent = {
                        var checked by remember {
                            if(enabled==0){
                                mutableStateOf(false)
                            }else{
                                mutableStateOf(true)
                            }
                        }

                        Switch(
                            checked = checked,
                            onCheckedChange = {
                                checked = it
                                if(it) {
                                    configResponse?.payload?.sys_prefs?.mqtt?.enabled=1
                                }else{
                                    configResponse?.payload?.sys_prefs?.mqtt?.enabled=0
                                }


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
                            text ="Broker IP",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    trailingContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = mqtt_broker_url,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.labelSmall
                            )

                            Spacer(modifier = Modifier.width(12.dp)) // Optional: Add space between the text and icon
                            Icon(
                                painter = painterResource(id = R.drawable.icon_arror),
                                contentDescription = "Navigate",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                    },
                    modifier = Modifier.clickable {

                        navHostController.navigate("broke_ip/$sn")
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
                            text ="Topics",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_arror),
                            contentDescription = "Navigate",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                    },
                    modifier = Modifier.clickable {

                        navHostController.navigate("topic/$sn")
                    }
                )



            }
        }

    }
}