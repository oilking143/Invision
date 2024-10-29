package com.intecular.invis.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.intecular.invis.common.ui.resource.theme.AppTheme

import com.intecular.invis.data.data.MainBottomBarData

@Composable
fun MainBottomBar(
    navController: NavHostController
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination

    AppTheme {
        Column {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                listOf(
                    MainBottomBarData.Home,
                    MainBottomBarData.Settings
                ).forEach { data ->
                    NavigationBarItem(
                        selected = currentRoute?.hierarchy?.any { it.route == data.route } == true,
                        onClick = {
                            navController.navigate(data.route) {
                                launchSingleTop = true
                            }
                        },
                        icon = {
                            val isCurrentRoute =
                                currentRoute?.hierarchy?.any { it.route == data.route } == true
                            AnimatedVisibility(
                                visible = isCurrentRoute,
                                enter = scaleIn(),
                                exit = scaleOut(),
                            ) {
                                Icon(
                                    painter = painterResource(id = if (isCurrentRoute) data.selectedIcon else data.notSelectedIcon),
                                    contentDescription = null,
                                )
                            }
                            AnimatedVisibility(
                                visible = !isCurrentRoute,
                                enter = scaleIn(),
                                exit = scaleOut(),
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isCurrentRoute) data.selectedIcon else data.notSelectedIcon

                                    ),
                                    contentDescription = null,
                                )
                            }

                        },
                        label = {
                            Text(
                                text = stringResource(id = data.titleId),
                                color = MaterialTheme.colorScheme.inverseSurface,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    )
                }
            }
        }
    }
}