package com.intecular.invis.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.intecular.invis.data.navigation.Graph
import com.intecular.invis.data.navigation.Screen
import kotlin.io.encoding.ExperimentalEncodingApi

@ExperimentalEncodingApi
@ExperimentalMaterial3Api
fun NavGraphBuilder.settingsGraph(
    navHostController: NavHostController,
) {
    navigation(startDestination = Screen.SettingsScreen.route, route = Graph.SettingsGraph.route) {
        composable(Screen.SettingsScreen.route) {
            SettingsScreen(navHostController)
        }
    }
}