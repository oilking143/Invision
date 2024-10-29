package com.intecular.invis.ui.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.common.ui.resource.theme.Typography

@ExperimentalMaterial3Api
@Composable
fun CommonChildTopAppBar(
    stringId: Int,
    navigationIconClicked: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(id = stringId),
                fontSize = 22.sp,
                style = Typography.titleLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                navigationIconClicked()
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_gray_left_arrow),
                    contentDescription = "back to previous page"
                )
            }
        }
    )
}