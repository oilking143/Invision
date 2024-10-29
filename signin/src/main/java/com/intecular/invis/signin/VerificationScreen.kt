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
import com.intecular.invis.data.entities.request.ConfirmSignupRequest
import com.intecular.invis.data.navigation.Screen

import com.intecular.invis.ui.view.CommonOutlinedTextField
import com.intecular.invis.signin.data.VerificationInfo

@ExperimentalMaterial3Api
@Composable
fun VerificationScreen(navHostController: NavHostController, verificationInfo: VerificationInfo) {
    Scaffold(
        topBar = {
            CommonTopAppBar(stringResource(id = R.string.verification)) {
                navHostController.popBackStack()
            }
        }
    ) { paddingValues ->
        val verificationViewModel: VerificationViewModel = hiltViewModel()
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onPrimary)
        ) {
            val verificationCode = remember {
                mutableStateOf("")
            }
            val showLoading =
                verificationViewModel.showLoading.collectAsState(initial = false)
            Box(
                Modifier
                    .padding(20.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Column(Modifier.align(Alignment.TopCenter)) {
                    CommonOutlinedTextField(
                        label = stringResource(id = R.string.six_digit_verification_code),
                        supportingText = stringResource(id = R.string.please_enter_the_six_digit_verification),
                        inputValue = verificationCode,
                        keyboardType = KeyboardType.Number
                    )
                }

                Button(
                    onClick = {
                        confirmSignup(
                            verificationViewModel,
                            verificationInfo,
                            verificationCode.value
                        )
                    },
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(10.dp, 24.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                        MaterialTheme.colorScheme.primary,
                        disabledContentColor = Color.Gray
                    ),
                    enabled = !showLoading.value && verificationCode.value.length == 6
                ) {
                    Text(
                        text = stringResource(id = R.string.verify),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    )
                }
                if (showLoading.value) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
            VerifySuccessObserve(navHostController)
            VerifyErrorDialog(verificationViewModel)
        }
    }
}

@Composable
fun VerifySuccessObserve(navHostController: NavHostController) {
    val verificationViewModel: VerificationViewModel = hiltViewModel()
    val verifyStates = verificationViewModel.verifySuccess.collectAsState(initial = false)
    if (verifyStates.value) {
        LaunchedEffect(Unit) {
            navHostController.navigate(Screen.SignInScreen.route)
        }
    }
}

fun confirmSignup(
    verificationViewModel: VerificationViewModel,
    verificationInfo: VerificationInfo,
    code: String
) {
    val confirmSignupRequest = ConfirmSignupRequest(
        confirmationCode = code,
        userName = verificationInfo.userName
    )
    verificationViewModel.confirmSignup(confirmSignupRequest)
}

@Composable
fun VerifyErrorDialog(verificationViewModel: VerificationViewModel) {
    val errorResponse =
        verificationViewModel.errorResponse.collectAsState(initial = null)
    val showErrorDialog = remember {
        mutableStateOf(false)
    }
    val errorMessage = verificationViewModel.errorMessage.collectAsState(initial = "")

    if (errorResponse.value?.message?.isNotEmpty() == true || errorMessage.value.isNotEmpty()) {
        showErrorDialog.value = true
    }
    if (showErrorDialog.value) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(
                    onClick = {
                        showErrorDialog.value = false
                        verificationViewModel.reductionErrorResponse()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text(
                        text = "Ok",
                        fontWeight = FontWeight.W500,
                        style = Typography.labelLarge,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            },
            title = {
                Text(
                    text = "Error!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.W400,
                    style = Typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = if (errorResponse.value?.message?.isNotEmpty() == true) errorResponse.value?.message
                        ?: "" else errorMessage.value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )
    }
}