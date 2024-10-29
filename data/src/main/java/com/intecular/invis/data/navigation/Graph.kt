package com.intecular.invis.data.navigation

sealed class Graph(val route: String) {
    data object NavHost : Graph("nav_host")
    data object HomeGraph : Graph("home-graph")
    data object AutomationGraph : Graph("automation-graph")
    data object SettingsGraph : Graph("settings-graph")
    data object SignInGraph : Graph("sign-in-graph")
}