package com.intecular.invis.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.common.ui.resource.theme.Typography
import com.intecular.invis.data.navigation.Screen
import kotlinx.coroutines.launch
import kotlin.io.encoding.ExperimentalEncodingApi

@ExperimentalMaterial3Api
@Composable
fun SettingsScreen(navHostController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    DetectLifeCycle(settingsViewModel)
    ModalNavigationDrawer(
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow),
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(modifier = Modifier.clip(RoundedCornerShape(24.dp))) {
                ModalDrawerContent(drawerState, settingsViewModel) {
                    navHostController.navigate("${Screen.HomeScreen.route}/$it")
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                    title = { Text(text = stringResource(id = R.string.settings)) },
                    navigationIcon = {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                Icons.Rounded.Menu,
                                contentDescription = "MenuButton"
                            )
                        }
                    },
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                SettingScreenContent(
                    settingsViewModel.settingItemList.collectAsState(initial = emptyList()).value,
                    navHostController,
                    settingsViewModel
                )

                val showLoading =
                    settingsViewModel.showLoading.collectAsState(initial = false)
                if (showLoading.value) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
            SettingErrorDialog(settingsViewModel)
        }
    }
}

@Composable
fun ModalDrawerContent(
    drawerState: DrawerState,
    settingsViewModel: SettingsViewModel,
    deviceRoomClicked: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Text(
        stringResource(id = com.intecular.invis.common.ui.resource.R.string.home),
        modifier = Modifier.padding(horizontal = 32.dp, vertical = 18.dp)
    )
    settingsViewModel.deviceRoomInfo.forEach { data ->
        NavigationDrawerItem(
            modifier = Modifier.padding(16.dp, 0.dp),
            label = {
                Text(text = data.deviceRoomName)
            },
            selected = false,
            onClick = {
                coroutineScope.launch { drawerState.close() }
                deviceRoomClicked(data.deviceRoomName)
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_white_empty_home),
                    contentDescription = "Device Room Icon"
                )
            }
        )
    }
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

@Composable
fun SettingScreenContent(
    settingItemList: List<SettingItemData>,
    navHostController: NavHostController,
    settingsViewModel: SettingsViewModel
) {
    val signInSuccess = settingsViewModel.signInSuccess.collectAsState(initial = ("" to ""))
    val userSignInClickedEnable =
        settingsViewModel.userSignInClickEnable.collectAsState(initial = false)
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onPrimary)
            .fillMaxSize()
            .padding(20.dp, 20.dp, 20.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = rememberRipple(),
                    enabled = userSignInClickedEnable.value
                ) {
                    if (signInSuccess.value
                            .toList()
                            .contains("")
                    ) {
                        navHostController.navigate(Screen.SignInScreen.route)
                    } else {
                        navHostController.navigate("${Screen.UserInfoScreen.route}/${signInSuccess.value.second}")
                    }
                }
        ) {
            SettingItem(
                signInSuccess.value.second.ifEmpty {
                    stringResource(
                        id = R.string.sign_in_to_your_invis_home
                    )
                },
                signInSuccess.value.first.ifEmpty {
                    stringResource(id = R.string.manage_devices_and_connect_services)
                },
                R.drawable.ic_person,
                true
            )
        }
        Column (
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            settingItemList.forEach {
                SettingItem(
                    it.titleString,
                    it.contentString,
                    it.iconId
                )
            }
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    content: String,
    iconId: Int,
    isSignIn: Boolean = false
) {
    ListItem(
        overlineContent = {
            Text(
                text = title,
                color =  if (isSignIn) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.onSurface,
                style = Typography.bodyLarge,
                fontWeight = if (isSignIn) FontWeight.Bold else FontWeight.SemiBold,
                )
        },
        headlineContent = {
            Text(
                text = content,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = content
            )
        },
        trailingContent = {
            Icon(
                painter = painterResource(id = R.drawable.ic_right_black_arrow),
                contentDescription = "Next step arrow icon"
            )
        }
    )
//    ConstraintLayout(
//        Modifier
//            .padding(5.dp, 16.dp)
//            .fillMaxWidth(),
//    ) {
//        val (itemIcon, textLayout, arrow) = createRefs()
//        Icon(
//            painter = painterResource(id = iconId),
//            contentDescription = "Setting item icon",
//            modifier = Modifier.constrainAs(itemIcon) {
//                start.linkTo(parent.start, margin = 10.dp)
//                top.linkTo(parent.top)
//                bottom.linkTo(parent.bottom)
//            },
//            tint = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//        Column(
//            modifier = Modifier
//                .constrainAs(textLayout) {
//                    start.linkTo(itemIcon.end, margin = 10.dp)
//                    top.linkTo(parent.top)
//                    bottom.linkTo(parent.bottom)
//                }
//        ) {
//            Text(
//                text = title,
//                fontSize = 16.sp,
//                style = if (isSignIn) Typography.labelLarge else Typography.bodyLarge,
//                fontWeight = if (isSignIn) FontWeight.SemiBold else FontWeight.W400,
//                color = if (isSignIn) MaterialTheme.colorScheme.surfaceTint else MaterialTheme.colorScheme.onSurface
//            )
//            Text(
//                text = content,
//                fontWeight = FontWeight.W400,
//                style = Typography.bodyMedium,
//                fontSize = 14.sp,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//        }
//        Icon(
//            painter = painterResource(id = R.drawable.ic_right_black_arrow),
//            contentDescription = "Next step arrow icon",
//            modifier = Modifier.constrainAs(arrow) {
//                end.linkTo(parent.end, margin = 10.dp)
//                top.linkTo(parent.top)
//                bottom.linkTo(parent.bottom)
//            },
//            tint = MaterialTheme.colorScheme.onSurfaceVariant
//        )
//    }
}

@Composable
fun DetectLifeCycle(settingsViewModel: SettingsViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                settingsViewModel.loginByRefreshToken()
            }
            if (event == Lifecycle.Event.ON_PAUSE) {
                settingsViewModel.reductionUserSignInClickEnable()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun SettingErrorDialog(settingViewModel: SettingsViewModel) {
    val errorResponse =
        settingViewModel.errorResponse.collectAsState(initial = null)
    val showErrorDialog = remember {
        mutableStateOf(false)
    }
    val errorMessage = settingViewModel.errorMessage.collectAsState(initial = "")
    if (errorResponse.value?.message?.isNotEmpty() == true || errorMessage.value.isNotEmpty()) {
        showErrorDialog.value = true
    }
    if (showErrorDialog.value) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(
                    onClick = {
                        showErrorDialog.value = false
                        settingViewModel.reductionErrorResponse()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        text = "Ok",
                        fontWeight = FontWeight.W500,
                        style = Typography.labelLarge,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            title = {
                Text(
                    text = "Error!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W400,
                    style = Typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = if (errorResponse.value?.message?.isNotEmpty() == true) errorResponse.value?.message
                        ?: "" else errorMessage.value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}