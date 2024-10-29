package com.intecular.invis.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.intecular.invis.common.ui.resource.R
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
import com.intecular.invis.common.ui.resource.theme.Typography
import com.intecular.invis.data.entities.request.LoginByAccountAuthParameters
import com.intecular.invis.data.entities.request.LoginByAccountRequest
import com.intecular.invis.data.navigation.Screen
import com.intecular.invis.ui.view.CommonOutlinedTextField
import com.intecular.invis.ui.view.CommonTopAppBar

@ExperimentalMaterial3Api
@Composable
fun SignInScreen(navHostController: NavHostController) {
    Scaffold(
        topBar = {
            CommonTopAppBar(titleText = stringResource(id = R.string.sign_in)) {
                navHostController.popBackStack()
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.onPrimary)
                .fillMaxSize()
        ) {
            val signInViewModel: SignInViewModel = hiltViewModel()
            Box(
                modifier = Modifier
                    .padding(20.dp, 20.dp, 20.dp, 20.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val email = remember {
                    mutableStateOf("")
                }
                val password = remember {
                    mutableStateOf("")
                }
                val showLoading =
                    signInViewModel.showLoading.collectAsState(initial = false)
                Column(
                    Modifier
                        .align(Alignment.TopCenter)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {


                    CommonOutlinedTextField(
                        label = stringResource(id = R.string.email),
                        inputValue = email,
                        keyboardType = KeyboardType.Email
                    )


                    CommonOutlinedTextField(
                        label = stringResource(id = R.string.password),
                        inputValue = password,
                        keyboardType = KeyboardType.Password
                    )

                    Text(
                        text = stringResource(id = R.string.forgot_your_password),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .clickable(
                                remember {
                                    MutableInteractionSource()
                                },
                                null
                            ) {
                                navHostController.navigate(
                                    Screen.ForgotPasswordScreen.route
                                )
                            }
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    BottomButton(
                        registerButtonClicked = {
                            navHostController.navigate(Screen.RegisterScreen.route)
                        },
                        signInButtonClicked = {
                            if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                                signIn(
                                    signInViewModel,
                                    email.value,
                                    password.value
                                )
                            }
                        },
                        signInEnable = email.value.isNotEmpty() && password.value.isNotEmpty(),
                        clickEnable = !showLoading.value
                    )
                }
                if (showLoading.value) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
            SignInObserve(signInViewModel, navHostController)
            SignInErrorDialog(signInViewModel)
        }
    }
}

@Composable
fun BottomButton(
    registerButtonClicked: () -> Unit,
    signInButtonClicked: () -> Unit,
    signInEnable: Boolean,
    clickEnable: Boolean
) {
    Row {
        OutlinedButton(
            onClick = { registerButtonClicked() },
            modifier = Modifier
                .weight(1f)
                .padding(10.dp, 24.dp),
            enabled = clickEnable,
        ) {
            Text(
                text = "Register",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Button(
            onClick = { signInButtonClicked() },
            modifier = Modifier
                .weight(1f)
                .padding(10.dp, 24.dp),
            colors = ButtonDefaults.buttonColors(disabledContentColor = Color.Gray),
            enabled = clickEnable && signInEnable // Disable Sign in button based on signInEnable
        ) {
            Text(
                text = stringResource(id = R.string.sign_in_button),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.W500,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun SignInObserve(signInViewModel: SignInViewModel, navHostController: NavHostController) {
    val signInSuccess = signInViewModel.signInSuccess.collectAsState(initial = "")
    if (signInSuccess.value.isNotEmpty()) {
        LaunchedEffect(Unit) {
            navHostController.navigate("${Screen.UserInfoScreen.route}/${signInSuccess.value}")
        }
    }
}

fun signIn(
    signInViewModel: SignInViewModel,
    email: String,
    password: String
) {
    val loginByAccountRequest = LoginByAccountRequest(
        authParameters = LoginByAccountAuthParameters(
            email,
            password
        )
    )
    signInViewModel.loginByAccount(loginByAccountRequest)
}

@Composable
fun SignInErrorDialog(signInViewModel: SignInViewModel) {
    val errorResponse =
        signInViewModel.errorResponse.collectAsState(initial = null)
    val showErrorDialog = remember {
        mutableStateOf(false)
    }
    val errorMessage = signInViewModel.errorMessage.collectAsState(initial = "")

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
                        signInViewModel.reductionErrorResponse()
                    },
                ) {
                    Text(
                        text = "Dismiss",
                    )
                }
            },
            title = {
                Text(
                    text = "Sign In Failed",
                )
            },
            text = {
                Text(
                    text = if (errorResponse.value?.message?.isNotEmpty() == true) errorResponse.value?.message
                        ?: "" else errorMessage.value,
                )
            },
        )
    }
}