package com.intecular.invis.home.DeviceSetting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.intecular.invis.home.home.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicScreen(navHostController: NavHostController, socketViewModel: SocketViewModel = hiltViewModel(), sn: String) {

    val topics = listOf(
        "Outlet 2 Subscribe Topic",
        "Outlet 2 Subscribe Topic",
        "Outlet Subscribe Topic",
        "Outlet 4 Subscribe Topic"
    )
    val sublines = listOf(
        "${sn.substring(sn.length-4,sn.length)}/outlet/first/ctrl",
        "${sn.substring(sn.length-4,sn.length)}/outlet/first/notify",
        "${sn.substring(sn.length-4,sn.length)}/outlet/second/ctrl",
        "${sn.substring(sn.length-4,sn.length)}/outlet/second/notify"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "MQTT Topics", color =  MaterialTheme.colorScheme.onSurface)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navHostController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "BackButton",
                            tint =  MaterialTheme.colorScheme.onSurface,
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor =  MaterialTheme.colorScheme.surfaceBright,// 设置TopAppBar背景色为白色
                    titleContentColor =  MaterialTheme.colorScheme.onSurface, // 设置标题文字颜色为黑色
                )

            )
        }
        , bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Button(
                    onClick = {

                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp
                        , start = 48.dp, end = 48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = "Save",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.surfaceVariant) ,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            LazyColumn(
                modifier = Modifier
                    .padding(25.dp)
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.surfaceBright,
                        shape = RoundedCornerShape(16.dp) )
                    .padding(25.dp)
                ,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                items(topics.size) { topic ->
                    ListItem(
                        colors = ListItemDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceBright,
                            headlineColor = MaterialTheme.colorScheme.onSurface,
                            leadingIconColor = MaterialTheme.colorScheme.onSurface,
                        ),
                        headlineContent = {
                            Text(
                                text = topics[topic],
                                modifier = Modifier
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        supportingContent = {
                            Text(
                                text = sublines[topic],
                                modifier = Modifier
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    )

                    if(topic<topics.size-1){
                        HorizontalDivider()
                    }

                }

            }

        }


    }
}
