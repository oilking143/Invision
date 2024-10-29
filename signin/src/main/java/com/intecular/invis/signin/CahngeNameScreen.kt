package com.intecular.invis.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.common.ui.resource.theme.Typography
import com.intecular.invis.ui.view.CommonOutlinedTextField
import com.intecular.invis.ui.view.CommonTopAppBar

@ExperimentalMaterial3Api
@Composable
fun ChangeNameScreen(navHostController: NavHostController) {

    Scaffold(
        topBar = {
            CommonTopAppBar(titleText = stringResource(id = R.string.change_name)) {
                navHostController.popBackStack()
            }
        }
    ) { paddingValues ->
        Box(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onPrimary)
        ) {
            Box(
                modifier = Modifier
                    .padding(20.dp, 40.dp, 20.dp, 20.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(Modifier.align(Alignment.TopCenter)) {
                    val name = remember {
                        mutableStateOf("")
                    }

                    CommonOutlinedTextField(
                        label = stringResource(id = R.string.name),
                        inputValue = name,
                        supportingText = stringResource(id = R.string.enter_new_name_to_change_your_account_name),
                        keyboardType = KeyboardType.Text
                    )
                }

                Button(
                    onClick = { },
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(10.dp, 24.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                        MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = stringResource(id = R.string.apply),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    )
                }
            }

        }
    }

}