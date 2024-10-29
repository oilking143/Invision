package com.intecular.invis.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.intecular.invis.ui.view.CommonTopAppBar
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.common.ui.resource.theme.Typography
import com.intecular.invis.data.entities.request.ForgetPasswordRequest
import com.intecular.invis.data.navigation.Screen
import com.intecular.invis.ui.view.CommonOutlinedTextField

@ExperimentalMaterial3Api
@Composable
fun ForgotPasswordScreen(navHostController: NavHostController) {
    Scaffold(
        topBar = {
            CommonTopAppBar(titleText = stringResource(id = R.string.forgot_password)) {
                navHostController.popBackStack()
            }
        }
    ) { paddingValues ->
        val forgotPasswordViewModel: ForgotPasswordViewModel = hiltViewModel()
        val showLoading =
            forgotPasswordViewModel.showLoading.collectAsState(initial = false)
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.onPrimary)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .padding(20.dp, 20.dp, 20.dp, 20.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val email = remember {
                    mutableStateOf("")
                }
                Column(Modifier.align(Alignment.TopCenter)) {
                    CommonOutlinedTextField(
                        label = stringResource(id = R.string.email),
                        inputValue = email,
                        supportingText = stringResource(id = R.string.reset_password_email_will_be_sent),
                        keyboardType = KeyboardType.Email
                    )
                }

                Button(
                    onClick = {
                        forgotPasswordViewModel.forgotPassword(ForgetPasswordRequest(email.value))
                    },
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(10.dp, 24.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                        MaterialTheme.colorScheme.error,
                        disabledContentColor = Color.Gray
                    ),
                    enabled = !showLoading.value
                ) {
                    Text(
                        text = stringResource(id = R.string.reset_password),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    )
                }
                if (showLoading.value) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                RequestVerifyCodeObserve(forgotPasswordViewModel, navHostController)
                ForgotPasswordErrorDialog(forgotPasswordViewModel)
            }
        }
    }
}

@Composable
fun RequestVerifyCodeObserve(
    forgotPasswordViewModel: ForgotPasswordViewModel,
    navHostController: NavHostController
) {
    val requestVerifyCodeSuccess = forgotPasswordViewModel.requestVerifyCodeSuccess.collectAsState(
        initial = ""
    )
    if (requestVerifyCodeSuccess.value.isNotEmpty()) {
        LaunchedEffect(Unit) {
            navHostController.navigate("${Screen.ResetPasswordScreen.route}/${requestVerifyCodeSuccess.value}")
        }
    }
}

@Composable
fun ForgotPasswordErrorDialog(forgotPasswordViewModel: ForgotPasswordViewModel) {
    val errorResponse =
        forgotPasswordViewModel.errorResponse.collectAsState(initial = null)
    val showErrorDialog = remember {
        mutableStateOf(false)
    }
    val errorMessage = forgotPasswordViewModel.errorMessage.collectAsState(initial = "")
    if (errorResponse.value?.message?.isNotEmpty() == true || errorMessage.value.isNotEmpty()) {
        showErrorDialog.value = true
    }
    if (showErrorDialog.value) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(
                    onClick = {
                        showErrorDialog.value = false
                        forgotPasswordViewModel.reductionErrorResponse()
                    },
                ) {
                    Text(
                        text = "Dismiss",
                    )
                }
            },
            title = {
                Text(
                    text = "Request Forgot Password Failed",
                )
            },
            text = {
                Text(
                    text = if (errorResponse.value?.message?.isNotEmpty() == true) errorResponse.value?.message
                        ?: "" else errorMessage.value,
                )
            }
        )
    }
}