package com.intecular.invis.data

import com.intecular.invis.data.data.DeviceRoomData
import com.intecular.invis.data.data.DeviceHomeDrawerInfo

data class RoomInfoUiState(
    val drawerInfoList: List<DeviceHomeDrawerInfo>,
    val deviceRoomItemList: List<DeviceRoomData>
)
