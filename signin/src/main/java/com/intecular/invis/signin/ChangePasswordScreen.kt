package com.intecular.invis.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.MutableState
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
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.common.ui.resource.theme.Typography
import com.intecular.invis.data.entities.request.ChangePasswordRequest
import com.intecular.invis.data.navigation.Screen
import com.intecular.invis.signin.common.DefaultVerifyPasswordMessage
import com.intecular.invis.ui.view.CommonOutlinedTextField
import com.intecular.invis.ui.view.CommonTopAppBar

@ExperimentalMaterial3Api
@Composable
fun ChangePasswordScreen(navHostController: NavHostController) {
    Scaffold(
        topBar = {
            CommonTopAppBar(titleText = stringResource(id = R.string.change_password)) {
                navHostController.popBackStack()
            }
        }
    ) { paddingValues ->
        val changePasswordViewModel: ChangePasswordViewModel = hiltViewModel()
        val oldPassword = remember {
            mutableStateOf("")
        }

        val newPassword = remember {
            mutableStateOf("")
        }
        val verifyColor = remember {
            mutableStateOf(Color.Unspecified)
        }
        val verifyMessage = remember {
            mutableStateOf("")
        }
        val verifyPassword = remember {
            mutableStateOf("")
        }
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
                Column(
                    Modifier.align(Alignment.TopCenter),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {


                    ChangePasswordTextFieldItem(
                        stringResource(id = R.string.old_password),
                        oldPassword,
                        stringResource(id = R.string.enter_your_old_password)
                    )



                    ChangePasswordTextFieldItem(
                        stringResource(id = R.string.new_password),
                        newPassword,
                        stringResource(id = R.string.enter_new_password_to_change_your_account_password)
                    )



                    ChangePasswordTextFieldItem(
                        label = stringResource(id = R.string.verify),
                        inputValue = verifyPassword,
                        descriptionContent = verifyMessage.value,
                        isVerifyPassword = true,
                        verifyPasswordColor = verifyColor.value
                    )

                }
                val showLoading =
                    changePasswordViewModel.showLoading.collectAsState(initial = false)

                Button(
                    onClick = {
                        changePasswordViewModel.checkNewPassword(
                            oldPassword.value,
                            newPassword.value,
                             verifyPassword.value
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
                    enabled = !showLoading.value
                ) {
                    Text(
                        text = stringResource(id = R.string.apply),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    )
                }

                if (showLoading.value) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                ChangePasswordObserve(
                    changePasswordViewModel,
                    navHostController,
                    verifyColor,
                    verifyMessage,
                    verifyPassword
                )
                ChangePasswordErrorDialog(changePasswordViewModel)
            }

        }
    }
}

@Composable
fun ChangePasswordObserve(
    changePasswordViewModel: ChangePasswordViewModel,
    navHostController: NavHostController,
    verifyColor: MutableState<Color>,
    verifyMessage: MutableState<String>,
    verifyPasswordValue: MutableState<String>
) {
    val changeSuccessful = changePasswordViewModel.changeSuccessful.collectAsState(initial = false)
    val passwordNotMatch = changePasswordViewModel.passwordNotMatch.collectAsState(initial = false)

    if (verifyPasswordValue.value.isEmpty()) {
        changePasswordViewModel.reductionPasswordNotMatchStatus()
        DefaultVerifyPasswordMessage(verifyColor, verifyMessage)
    }

    if (passwordNotMatch.value) {
        verifyColor.value = MaterialTheme.colorScheme.error
        verifyMessage.value = stringResource(id = R.string.password_does_not_match)
    } else {
        DefaultVerifyPasswordMessage(verifyColor, verifyMessage)
    }

    if (changeSuccessful.value) {
        LaunchedEffect(Unit) {
            navHostController.navigate(Screen.SignInScreen.route)
        }
    }
}

@Composable
fun ChangePasswordTextFieldItem(
    label: String,
    inputValue: MutableState<String>,
    descriptionContent: String,
    keyboardType: KeyboardType = KeyboardType.Password,
    isVerifyPassword: Boolean = false,
    verifyPasswordColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        CommonOutlinedTextField(label = label, inputValue = inputValue, keyboardType = keyboardType)
        Text(
            modifier = Modifier
                .padding(10.dp, 0.dp)
                .fillMaxWidth(),
            text = descriptionContent,
            fontSize = 12.sp,
            style = Typography.bodySmall,
            fontWeight = FontWeight.W400,
            color = if (isVerifyPassword) {
                verifyPasswordColor
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
fun ChangePasswordErrorDialog(changePasswordViewModel: ChangePasswordViewModel) {
    val errorResponse =
        changePasswordViewModel.errorResponse.collectAsState(initial = null)
    val showErrorDialog = remember {
        mutableStateOf(false)
    }
    val errorMessage = changePasswordViewModel.errorMessage.collectAsState(initial = "")
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
                        changePasswordViewModel.reductionErrorResponse()
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