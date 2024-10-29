package com.intecular.invis.automation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.intecular.invis.data.navigation.Graph
import com.intecular.invis.data.navigation.Screen


fun NavGraphBuilder.automationGraph(
    navHostController: NavHostController
) {
    navigation(startDestination = Screen.AutomationScreen.route, route = Graph.AutomationGraph.route) {
        composable(Screen.AutomationScreen.route) {

        }
    }
}