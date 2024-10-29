package com.intecular.invis.data.data

import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.data.navigation.Graph

sealed class MainBottomBarData(
    val route: String,
    val titleId: Int,
    val notSelectedIcon: Int,
    val selectedIcon: Int,

) {
    data object Home : MainBottomBarData(
        Graph.HomeGraph.route,
        R.string.home,
        R.drawable.ic_white_empty_home,
        R.drawable.ic_black_filled_home


    )

    data object Automation : MainBottomBarData(
        Graph.AutomationGraph.route,
        R.string.automation,
       0,
        0
    )

    data object Settings : MainBottomBarData(
        Graph.SettingsGraph.route,
        R.string.settings,
        R.drawable.ic_white_empty_settings,
        R.drawable.ic_black_filled_settings
    )
}