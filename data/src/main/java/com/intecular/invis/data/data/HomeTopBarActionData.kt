package com.intecular.invis.data.data

import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.data.navigation.Screen

sealed class HomeTopBarActionData(
    val title: String,
    val iconId: Int,
    val route: String
) {
    data object AddDevice : HomeTopBarActionData(
        "Add Device",
        R.drawable.ic_gray_light_bulb,
        Screen.AddDeviceScreen.route
    )

    data object AddRoom : HomeTopBarActionData(
        "Add Room",
        R.drawable.ic_gray_sofa,
        Screen.AddRoomScreen.route
    )

    data object AddHouse : HomeTopBarActionData(
        "Add House",
        R.drawable.ic_gray_house,
        Screen.AddHouseScreen.route
    )

    data object EditHomeScreen : HomeTopBarActionData(
        "Edit Home Screen",
        R.drawable.ic_gray_edit,
        Screen.EditHomeScreen.route
    )


}