package com.intecular.invis.home.adddevice

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.ui.view.CommonChildTopAppBar

@ExperimentalMaterial3Api
@Composable
fun AddRoomScreen(
    navHostController: NavHostController

) {
    Scaffold(topBar = {
        CommonChildTopAppBar(stringId = R.string.add_room) {
            navHostController.popBackStack()
        }
    }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {

        }
    }
}