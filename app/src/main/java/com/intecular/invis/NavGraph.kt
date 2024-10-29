package com.intecular.invis

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.intecular.invis.automation.automationGraph
import com.intecular.invis.common.ui.resource.theme.AppTheme
import com.intecular.invis.data.navigation.Graph
import com.intecular.invis.home.home.homeGraph
import com.intecular.invis.settings.settingsGraph
import com.intecular.invis.signin.signInGraph
import kotlin.io.encoding.ExperimentalEncodingApi

@ExperimentalEncodingApi
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@ExperimentalWearMaterialApi
@Composable
fun NavGraph(navHostController: NavHostController) {
    AppTheme {
        NavHost(
            navController = navHostController,
            startDestination = Graph.HomeGraph.route
        ) {
            homeGraph(navHostController)
            automationGraph(navHostController)
            settingsGraph(navHostController)
            signInGraph(navHostController)
        }
    }
}