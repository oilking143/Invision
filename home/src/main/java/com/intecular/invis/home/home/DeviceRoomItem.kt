package com.intecular.invis.home.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.data.data.DeviceInfo
import com.intecular.invis.data.data.DeviceRoomData
import com.intecular.invis.data.data.DeviceStatusInfo
import com.intecular.invis.data.data.RoomStatusInfo
import timber.log.Timber

@ExperimentalFoundationApi
@Composable
fun RoomItem(deviceRoomData: DeviceRoomData,homeViewModel: HomeViewModel, brightnessClicked: (openStatus: Int) -> Unit,
             refreshClick: () -> Unit, settingClick: (position: Int, deviceInfo: DeviceInfo) -> Unit ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(0.dp, vertical = 16.dp)
            ) {
                deviceRoomData.deviceList.forEachIndexed  {index, deviceInfo ->
                    DeviceInfoItem(deviceInfo,homeViewModel, brightnessClicked,refreshClick,settingClick = {
                        settingClick(index, deviceInfo) // 在点击时传递index和deviceInfo
                    })
                }
            }


}


@ExperimentalFoundationApi
@Composable
fun DeviceInfoItem(deviceInfo: DeviceInfo,homeViewModel: HomeViewModel, brightnessClicked: (openStatus: Int) -> Unit,
     refreshClick: () -> Unit, settingClick: () -> Unit) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceBright, RoundedCornerShape(15.dp))
    ) {
        DeviceInfoTitle(deviceInfo,homeViewModel, brightnessClicked,refreshClick,settingClick)
        DeviceStatusRow(
            GetDeviceStatusRowData(deviceInfo,homeViewModel)
        )
        DeviceOpenStatusGroup(homeViewModel)
    }
}

@ExperimentalFoundationApi
@Composable
fun DeviceInfoTitle(deviceInfo: DeviceInfo,homeViewModel: HomeViewModel, brightnessClicked: (openStatus: Int) -> Unit
                    , refreshClick: () -> Unit, settingClick: () -> Unit) {
    val nightLightStatu = remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp, 12.dp)
    ) {
        Icon(
            painter = painterResource(id = if (nightLightStatu.value) R.drawable.ic_open_status_bulb else R.drawable.ic_close_status_bulb),
            contentDescription = "Open status icon",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .combinedClickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    }, indication = null, onClick = {
                        if (nightLightStatu.value){
                            nightLightStatu.value=false
                            homeViewModel.setnightLightStatus(1,100)
                        } else{
                            nightLightStatu.value=true
                            homeViewModel.setnightLightStatus(0,0)
                        }


                    }, onLongClick = {
                        brightnessClicked(deviceInfo.status)
                    })
        )
        Text(
            text = deviceInfo.deviceName,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Center),
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_refresh),
                contentDescription = "Refresh data icon",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.clickable {
                    refreshClick()
                }
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "Settings icon",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(4.dp, 0.dp).clickable {
                    settingClick()
                }
            )
        }
    }
}

@Composable
fun DeviceStatusRow(deviceStatusInfoList: List<DeviceStatusInfo>) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(6.dp, 0.dp, 6.dp, 0.dp)
    ) {
        deviceStatusInfoList.forEach { deviceStatusInfo ->
            DeviceStatusRowItem(deviceStatusInfo)
        }
    }
}

@Composable
fun DeviceStatusRowItem(deviceStatusInfo: DeviceStatusInfo) {
    Column(modifier = Modifier.padding(horizontal = 5.dp, vertical = 0.dp)) {
        OutlinedCard(
            shape = RoundedCornerShape(50),
            border = BorderStroke(
                deviceStatusInfo.borderWidth.dp,
                deviceStatusInfo.borderColorId
            ),
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 4.dp)
        ) {
            Icon(
                painter = painterResource(id = deviceStatusInfo.deviceStatusIconId),
                contentDescription = "Device status icon",
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 5.dp)
            )
        }


        Text(
            text = deviceStatusInfo.deviceStatusContent,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            lineHeight = TextUnit(12F, TextUnitType.Sp),
            color = deviceStatusInfo.borderColorId
        )
    }
}

@Composable
fun DeviceOpenStatusGroup(homeViewModel: HomeViewModel) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val infoResponse by homeViewModel.deviceInfoLiveData.observeAsState()
        val outletResponse by homeViewModel.outletInfoLiveData.observeAsState()
        val leftTurnOnButtonChecked = remember {
            mutableStateOf(false)
        }
        val rightTurnOnButtonChecked = remember {
            mutableStateOf(false)
        }
        LaunchedEffect(infoResponse) {
            infoResponse?.let { response ->
                leftTurnOnButtonChecked.value = response.payload.callbackArgs.PM.online
                rightTurnOnButtonChecked.value = response.payload.callbackArgs.PM.online
            }
        }
        Row(
            modifier = Modifier
                .background(
                    if (!leftTurnOnButtonChecked.value) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(15.dp)
                )
                .fillMaxSize()
                .weight(1f)
                .padding(0.dp, 10.dp)
                .toggleable(
                    leftTurnOnButtonChecked.value,
                    remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onValueChange = { isChecked ->
                        leftTurnOnButtonChecked.value = isChecked
                        if(isChecked){
                            homeViewModel.setOutLetStatus(1,1)
                        }else{
                            homeViewModel.setOutLetStatus(1,0)
                        }
                    }
                )
        ) {
            Icon(
                painter = painterResource(id = if (!leftTurnOnButtonChecked.value) R.drawable.ic_accessory_off else R.drawable.ic_accessory_on),
                contentDescription = "Open device status icon",
                tint = if (!leftTurnOnButtonChecked.value) MaterialTheme.colorScheme.onSecondaryContainer else Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp, 0.dp, 0.dp, 0.dp)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Outlet 1",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (!leftTurnOnButtonChecked.value) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = stringResource(id = if (!leftTurnOnButtonChecked.value) R.string.outlet_status_off else R.string.outlet_status_on),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (!leftTurnOnButtonChecked.value) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Row(
            modifier = Modifier
                .background(
                    if (!rightTurnOnButtonChecked.value) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(15.dp)
                )
                .fillMaxSize()
                .weight(1f)
                .padding(0.dp, 10.dp)
                .toggleable(
                    rightTurnOnButtonChecked.value,
                    remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onValueChange = { isChecked ->
                        rightTurnOnButtonChecked.value = isChecked
                        if(isChecked){
                            homeViewModel.setOutLetStatus(2,1)
                        }else{
                            homeViewModel.setOutLetStatus(2,0)
                        }
                    }
                )
        ) {
            Icon(
                painter = painterResource(id = if (!rightTurnOnButtonChecked.value) R.drawable.ic_accessory_off else R.drawable.ic_accessory_on),
                contentDescription = "Open device status icon",
                tint = if (!rightTurnOnButtonChecked.value) MaterialTheme.colorScheme.onSecondaryContainer else Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp, 0.dp, 0.dp, 0.dp)
            )
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(8.dp, vertical = 0.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Outlet 2",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (!rightTurnOnButtonChecked.value) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(id = if (!rightTurnOnButtonChecked.value) R.string.outlet_status_off else R.string.outlet_status_on),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (!rightTurnOnButtonChecked.value) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

    }
}



@Composable
private fun GetDeviceStatusRowData(deviceInfo: DeviceInfo,homeViewModel: HomeViewModel): List<DeviceStatusInfo> {
    val roomData by homeViewModel.roomDataLiveData.observeAsState(emptyList())
    return listOf(
        DeviceStatusInfo(
            if (deviceInfo.motionDetected == 1) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error,
            if (deviceInfo.motionDetected == 1) 0.5f else 1.0f,
            if (deviceInfo.motionDetected == 1) R.drawable.ic_motion_not_detected else R.drawable.ic_motion_detected,
            deviceStatusContent = if (deviceInfo.motionDetected == 1) "Motion not \ndetected" else "Motion \ndetected"
        ),
        DeviceStatusInfo(
            if (deviceInfo.occupied == 1) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error,
            if (deviceInfo.occupied == 1) 0.5f else 1.0f,
            if (deviceInfo.occupied == 1) R.drawable.ic_not_occupied else R.drawable.ic_occupied,
            deviceStatusContent = if (deviceInfo.motionDetected == 1) "Not \noccupied" else "Occupied"
        ),
        DeviceStatusInfo(
            MaterialTheme.colorScheme.error,
            1.0f,
            R.drawable.ic_distance,
            deviceStatusContent ="Distance\n\r"+ deviceInfo.distance.toString()+"in"
        ),
        DeviceStatusInfo(
            if (deviceInfo.airQualityIndex == "Hazardous") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimaryContainer,
            when (deviceInfo.airQualityIndex) {
                "Hazardous" -> 1.0f
                else -> 0.5f
            },
            if(deviceInfo.airQualityIndex.contains("Execellent"))
                R.drawable.ic_first_air_quality_status
            else  if(deviceInfo.airQualityIndex.contains("Good"))
                R.drawable.ic_second_air_quality_status
            else  if(deviceInfo.airQualityIndex.contains("Average"))
                R.drawable.ic_second_air_quality_status
            else  if(deviceInfo.airQualityIndex.contains("Poor"))
                R.drawable.ic_second_air_quality_status
            else  if(deviceInfo.airQualityIndex.contains("Hazardous"))
                R.drawable.ic_device_air_quality_fifth_status
            else
                R.drawable.ic_aqi_noe_sev
            ,
            deviceInfo.airQualityIndex
        ),
        DeviceStatusInfo(
            if (deviceInfo.temperature >79.2) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error,
            if (deviceInfo.temperature >79.2) 1.0f else 0.5f,
            if(deviceInfo.temperature<60.8)
                R.drawable.ic_temperature_fitst_status
            else if(deviceInfo.temperature>60.8 && deviceInfo.temperature<79.7)
                R.drawable.ic_temperature_second_status
            else
                R.drawable.ic_temperature_third_status,
            deviceInfo.temperature.toString()+"ºF"
        ),
        DeviceStatusInfo(
            if (deviceInfo.humidity.toFloat() < 79) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error,
            if (deviceInfo.humidity.toFloat() < 66) 0.5f else 1.0f,
            if (deviceInfo.humidity.toFloat() < 66) R.drawable.ic_device_humidity_first_status else R.drawable.ic_device_humidity_second_status,
            deviceInfo.humidity.toString()+"%"
        ),
        DeviceStatusInfo(
            MaterialTheme.colorScheme.onPrimaryContainer,
            0.5f,
            if (deviceInfo.lux.toFloat()>=100) R.drawable.ic_device_lux_first_status else R.drawable.ic_device_lux_second_status,
            deviceInfo.lux.toString()+"\n\rLUX"
        ),
        DeviceStatusInfo(
            if (deviceInfo.voc.toFloat() <=2.2) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.error,
            if (deviceInfo.voc.toFloat() <=2.2 ) 0.5f else 1.0f,
            if (deviceInfo.voc.toFloat() <=2.2 ) R.drawable.ic_device_voc_first_status else R.drawable.ic_device_voc_second_status,
            deviceInfo.voc+"mg/m3\n\rVOC"
        ),
        DeviceStatusInfo(
            MaterialTheme.colorScheme.onPrimaryContainer ,
            0.5f,
            R.drawable.ic_co2_3x,
            deviceInfo.co2.toString()+"\n\rPPM"
        )
        ,
        DeviceStatusInfo(
            MaterialTheme.colorScheme.onPrimaryContainer ,
            0.5f,
            R.drawable.ic_pressure_3x,
            deviceInfo.pressure.toString()+"\n\rhPa"
        )
    )
}



