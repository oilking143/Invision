package com.intecular.invis.signin

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.intecular.invis.data.navigation.Graph
import com.intecular.invis.data.navigation.Screen
import com.intecular.invis.signin.data.VerificationInfo
import kotlin.io.encoding.ExperimentalEncodingApi

@ExperimentalEncodingApi
@ExperimentalMaterial3Api
fun NavGraphBuilder.signInGraph(navHostController: NavHostController) {
    navigation(startDestination = Screen.SignInScreen.route, route = Graph.SignInGraph.route) {
        composable(Screen.SignInScreen.route) {
            SignInScreen(navHostController)
        }

        composable(Screen.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(navHostController = navHostController)
        }

        composable(Screen.RegisterScreen.route) {
            RegisterScreen(navHostController)
        }

        composable<VerificationInfo> { backStackEntry ->
            val verificationInfo: VerificationInfo = backStackEntry.toRoute()
            VerificationScreen(navHostController, verificationInfo)
        }

        composable(
            "${Screen.UserInfoScreen.route}/{userName}",
            arguments = listOf(navArgument("userName") { defaultValue = "" })
        ) { backStackEntry ->
            UserInfoScreen(
                navHostController,
                backStackEntry.arguments?.getString("userName") ?: ""
            )
        }

        composable(Screen.ChangeNameScreen.route) {
            ChangeNameScreen(navHostController)
        }

        composable(Screen.ChangePasswordScreen.route) {
            ChangePasswordScreen(navHostController)
        }

        composable(
            "${Screen.ResetPasswordScreen.route}/{email}",
            arguments = listOf(navArgument("email") {
                type = NavType.StringType
                defaultValue = ""
            }),
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email", "") ?: ""
            ResetPasswordScreen(navHostController, email)
        }
    }
}