package com.intecular.invis.ui.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.common.ui.resource.theme.Typography

@ExperimentalMaterial3Api
@Composable
fun CommonTopAppBar(
    titleText: String,
    backPressClicked: () -> Unit
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
        title = {
            Text(
                text = titleText,
                fontSize = 22.sp,
                fontWeight = FontWeight.W400,
                style = Typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = { backPressClicked() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gray_left_arrow),
                    contentDescription = "Back press icon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

        }
    )
}