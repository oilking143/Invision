package com.intecular.invis

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import com.intecular.invis.common.ui.resource.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlin.io.encoding.ExperimentalEncodingApi

@ExperimentalComposeUiApi
@ExperimentalWearMaterialApi
@ExperimentalFoundationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalEncodingApi
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navHostController = rememberNavController()
                NavGraph(navHostController)
            }
        }
    }
}