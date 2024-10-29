package com.intecular.invis.home.DeviceSetting
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.intecular.invis.base.ext.getActivity
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.data.navigation.Screen
import com.intecular.invis.home.Api.ApiClient
import com.intecular.invis.home.home.HomeViewModel
import com.intecular.invis.home.home.Tcpdatas.deviceConfig.Response.Request.Payload
import com.intecular.invis.home.home.Tcpdatas.deviceConfig.Response.Request.deviceRequest
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@ExperimentalMaterial3Api
@Composable
fun DeviceSettingScreen(navHostController: NavHostController,socketViewModel: SocketViewModel = hiltViewModel(),sn:String,device:String) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val settingOptions = stringArrayResource(id = R.array.setting_option)
    val serial = sn
    var deviceName = device

    val infoResponse by socketViewModel.deviceInfoLiveData.observeAsState()
    LaunchedEffect(Unit) {
        if (infoResponse == null) {
            socketViewModel.connectToService(serial, deviceName)
        }
    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.clip(RoundedCornerShape(24.dp))) {
                ModalDrawerContent(drawerState, socketViewModel) {
                    navHostController.navigate("${Screen.HomeScreen.route}/$it")
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if(infoResponse!=null){
                    deviceName = infoResponse!!.payload.callbackArgs.IM.MAC
                }
                TopAppBar(

                    title = { Text(text = "$deviceName "+stringResource(id = R.string.settings)
                        , color =  MaterialTheme.colorScheme.onSurface) },
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
                        containerColor =  MaterialTheme.colorScheme.surfaceBright,
                        titleContentColor =  MaterialTheme.colorScheme.surface
                    ),
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background( MaterialTheme.colorScheme.surfaceVariant)
            ){
                Column(modifier = Modifier.padding(paddingValues)) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background( MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        LazyColumn {
                            itemsIndexed(settingOptions) { index, option ->

                                ListItem(
                                    colors = ListItemDefaults.colors(
                                        containerColor = MaterialTheme.colorScheme.surfaceBright,
                                        headlineColor = MaterialTheme.colorScheme.onSurface,
                                        leadingIconColor = MaterialTheme.colorScheme.onSurface,
                                    ),
                                    headlineContent = {
                                        Text(
                                            text = option,
                                            color =  MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.bodyLarge,

                                            )
                                    },
                                    trailingContent = {
                                        Icon(
                                            painter = painterResource(id = R.drawable.icon_arror),
                                            contentDescription = "Navigate",
                                            tint =  MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    modifier = Modifier.clickable {

                                        when (index) {
                                            0 -> navHostController.navigate("edit_device_name/$deviceName")
                                            1 -> navHostController.navigate("edit_device_details/$deviceName")
                                            2 -> navHostController.navigate("customizations/$deviceName")
                                            3 -> navHostController.navigate("device_info/$deviceName")
                                            4 -> navHostController.navigate("soft_update/$deviceName")
                                            5 -> navHostController.navigate("device_manager/$deviceName")

                                            // 添加更多的导航操作
                                        }
                                    }
                                )


                            }
                        }
                    }

                }
            }

        }
    }
}


@Composable
fun ModalDrawerContent(
    drawerState: DrawerState,
    socketViewModel: SocketViewModel,
    deviceRoomClicked: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Text(
        stringResource(id = com.intecular.invis.common.ui.resource.R.string.home),
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 18.dp)
    )

    HorizontalDivider(
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 0.dp)
    )

    NavigationDrawerItem(
        modifier = Modifier.padding(16.dp, 0.dp),
        label = {
            Text(text = stringResource(id = R.string.refresh))
        }, selected = false, onClick = {
            // TODO: 呼叫取得裝置Api
            coroutineScope.launch {
                drawerState.close()
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_refresh),
                contentDescription = "Device Room Icon"
            )
        }
    )
    HorizontalDivider(
        modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 16.dp)
    )
    NavigationDrawerItem(
        modifier = Modifier.padding(16.dp, 0.dp),
        label = {
            Text(text = stringResource(id = R.string.settings))
        }, selected = true, onClick = {
            coroutineScope.launch {
                drawerState.close()
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "Device Room Icon"
            )
        }
    )
}
