package com.intecular.invis.home.home

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.intecular.invis.data.navigation.Graph
import com.intecular.invis.data.navigation.Screen
import com.intecular.invis.home.DeviceSetting.BrokeIPScreen
import com.intecular.invis.home.DeviceSetting.CustomizationsScreen
import com.intecular.invis.home.DeviceSetting.DataInfoScreen
import com.intecular.invis.home.DeviceSetting.DeviceManager
import com.intecular.invis.home.DeviceSetting.DeviceSettingScreen
import com.intecular.invis.home.DeviceSetting.EditDeviceDetailsScreen
import com.intecular.invis.home.DeviceSetting.EditDeviceNameScreen
import com.intecular.invis.home.DeviceSetting.MqttScreen
import com.intecular.invis.home.DeviceSetting.ProgressScreen
import com.intecular.invis.home.DeviceSetting.SocketViewModel
import com.intecular.invis.home.DeviceSetting.TopicScreen
import com.intecular.invis.home.DeviceSetting.UpdateScreen
import com.intecular.invis.home.adddevice.AddDeviceScreen
import com.intecular.invis.home.adddevice.AddHouseScreen
import com.intecular.invis.home.adddevice.AddRoomScreen
import timber.log.Timber

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@ExperimentalComposeUiApi
@ExperimentalFoundationApi
@ExperimentalWearMaterialApi
@OptIn(ExperimentalWearMaterialApi::class)
@ExperimentalMaterial3Api
fun NavGraphBuilder.homeGraph(
    navHostController: NavHostController,
) {
    navigation(
        startDestination = "${Screen.HomeScreen.route}/{device_room_name}",
        route = Graph.HomeGraph.route
    ) {
        composable(
            "${Screen.HomeScreen.route}/{device_room_name}"
        ) {
            HomeScreen(navHostController = navHostController)
            Timber.i("Get end:${it.arguments?.getString("device_room_name")}")

        }

        composable(Screen.DviceSettingsScreen.route+"/{param}/{device}"
            ,    arguments = listOf(
                navArgument("param") { type = NavType.StringType },
                navArgument("device") { type = NavType.StringType }
            )) {
                backStackEntry ->
            val param = backStackEntry.arguments?.getString("param").orEmpty()
            val device = backStackEntry.arguments?.getString("device").orEmpty()
            val socketViewModel:SocketViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
            DeviceSettingScreen(navHostController,socketViewModel,param,device)
        }

        composable(Screen.EditDeviceNameScreen.route+"/{param}"
            ,arguments = listOf(navArgument("param") { type = NavType.StringType })) {
                backStackEntry ->
            var param = ""
            val socketViewModel:SocketViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
            param = backStackEntry.arguments?.getString("param").toString()
            EditDeviceNameScreen(navHostController,socketViewModel,param)
        }

        composable(Screen.EditDeviceDetailsScreen.route+"/{param}"
            ,arguments = listOf(navArgument("param") { type = NavType.StringType })) {
                backStackEntry ->
            var param = ""
            val socketViewModel:SocketViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
            param = backStackEntry.arguments?.getString("param").toString()
            EditDeviceDetailsScreen(navHostController,socketViewModel,param)
        }

        composable( route = Screen.CustomizationsScreen.route+"/{param}"
            ,arguments = listOf(navArgument("param") { type = NavType.StringType })) {
                backStackEntry ->
            val socketViewModel:SocketViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
            val param = backStackEntry.arguments?.getString("param")
            if (param != null) {
                CustomizationsScreen(navHostController,socketViewModel, param)
            }else{
                CustomizationsScreen(navHostController,socketViewModel, "")
            }
        }

        composable(Screen.MQTTScreen.route+"/{param}"
            ,arguments = listOf(navArgument("param") { type = NavType.StringType })) {
                backStackEntry ->
            var param = ""
            val socketViewModel:SocketViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
            param = backStackEntry.arguments?.getString("param").toString()
            MqttScreen(navHostController,socketViewModel,param)
        }


        composable(Screen.BrokeScreen.route+"/{param}"
            ,arguments = listOf(navArgument("param") { type = NavType.StringType })) {
                backStackEntry ->
            var param = ""
            val socketViewModel:SocketViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
            param = backStackEntry.arguments?.getString("param").toString()
            BrokeIPScreen(navHostController,socketViewModel,param)
        }

        composable(Screen.TopicScreen.route+"/{param}"
            ,arguments = listOf(navArgument("param") { type = NavType.StringType })) {
                backStackEntry ->
            var param = ""
            val socketViewModel:SocketViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
            param = backStackEntry.arguments?.getString("param").toString()
            TopicScreen(navHostController,socketViewModel,param)
        }

        composable(Screen.DeviceInfoScreen.route+"/{param}"
            ,arguments = listOf(navArgument("param") { type = NavType.StringType })) {
                backStackEntry ->
            var param = ""
            val socketViewModel:SocketViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
            param = backStackEntry.arguments?.getString("param").toString()
            DataInfoScreen(navHostController,socketViewModel,param)
        }

        composable(Screen.UpdateScreen.route+"/{param}"
            ,arguments = listOf(navArgument("param") { type = NavType.StringType })) {
                backStackEntry ->
            var param = ""
            val socketViewModel:SocketViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
            param = backStackEntry.arguments?.getString("param").toString()
            UpdateScreen(navHostController,socketViewModel,param)
        }

        composable(Screen.DeviceManagerScreen.route+"/{param}"
            ,arguments = listOf(navArgument("param") { type = NavType.StringType })) {
                backStackEntry ->
            var param = ""
            val socketViewModel:SocketViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
            param = backStackEntry.arguments?.getString("param").toString()
            DeviceManager(navHostController,socketViewModel,param)
        }

        composable(Screen.ProgressScreen.route+"/{param}/{select}"
            ,arguments = listOf(navArgument("param") { type = NavType.StringType })) {
                backStackEntry ->
            val socketViewModel:SocketViewModel = hiltViewModel(LocalContext.current as ComponentActivity)
            val param = backStackEntry.arguments?.getString("param").orEmpty()
            val select = backStackEntry.arguments?.getString("select").orEmpty()
            ProgressScreen(navHostController,socketViewModel,param,select)
        }

        composable(Screen.AddDeviceScreen.route) {
            AddDeviceScreen(navHostController)
        }

        composable(Screen.AddRoomScreen.route) {
            AddRoomScreen(navHostController)
        }

        composable(Screen.AddHouseScreen.route) {
            AddHouseScreen(navHostController)
        }

    }

}