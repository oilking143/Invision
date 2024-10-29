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
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.common.ui.resource.theme.Typography
import com.intecular.invis.data.entities.request.SignupRequest
import com.intecular.invis.data.entities.request.UserAttribute
import com.intecular.invis.ui.view.CommonOutlinedTextField
import com.intecular.invis.ui.view.CommonTopAppBar


@ExperimentalMaterial3Api
@Composable
fun RegisterScreen(navHostController: NavHostController) {
    val registerScreenViewModel: RegisterScreenViewModel = hiltViewModel()
    Scaffold(
        topBar = {
            CommonTopAppBar(titleText = stringResource(id = R.string.register)) {
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
                    .padding(20.dp)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val name = remember {
                    mutableStateOf("")
                }

                val email = remember {
                    mutableStateOf("")
                }

                val password = remember {
                    mutableStateOf("")
                }

                val showLoading =
                    registerScreenViewModel.showLoading.collectAsState(initial = false)
                Column(
                    Modifier.align(Alignment.TopCenter),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {


                    CommonOutlinedTextField(
                        label = stringResource(id = R.string.name),
                        inputValue = name,
                        keyboardType = KeyboardType.Text
                    )


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
                    Column {
                        Text(
                            text = stringResource(id = R.string.password_requirements),
                            fontSize = 14.sp,
                            style = Typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Contains at least 1 number\nContains at least 1 uppercase letter\nContains at least 1 lowercase letter\nPassword minimum is at least 8 characters",
                            style = Typography.bodySmall,
                            fontWeight = FontWeight.W400,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = {
                        if (name.value.isNotEmpty() && email.value.isNotEmpty() && password.value.isNotEmpty()) {
                            signupAccount(
                                registerScreenViewModel,
                                name.value,
                                password.value,
                                email.value
                            )
                        }
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
                    enabled = !showLoading.value && ( name.value.isNotEmpty() && email.value.isNotEmpty() && password.value.isNotEmpty() )
                ) {
                    Text(
                        text = stringResource(id = R.string.create_account),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W500
                    )
                }

                if (showLoading.value) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }
            SignupObserve(registerScreenViewModel, navHostController)
            SignupErrorDialog(registerScreenViewModel)
        }
    }
}

@Composable
private fun SignupObserve(
    registerScreenViewModel: RegisterScreenViewModel,
    navHostController: NavHostController
) {
    val signUpSuccess =
        registerScreenViewModel.signupSuccess.collectAsState(initial = null)
    if (signUpSuccess.value != null) {
        LaunchedEffect(key1 = Unit) {
            navHostController.navigate(signUpSuccess.value!!)
        }
    }
}

@Composable
fun SignupErrorDialog(registerScreenViewModel: RegisterScreenViewModel) {
    val errorResponse =
        registerScreenViewModel.errorResponse.collectAsState(initial = null)
    val showErrorDialog = remember {
        mutableStateOf(false)
    }
    val errorMessage = registerScreenViewModel.errorMessage.collectAsState(initial = "")
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
                        registerScreenViewModel.reductionErrorResponse()
                    },
                ) {
                    Text(
                        text = "Dismiss",
                    )
                }
            },
            title = {
                Text(
                    text = "Register Failed",
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

fun signupAccount(
    registerScreenViewModel: RegisterScreenViewModel,
    name: String,
    password: String,
    email: String
) {
    val signupAccountRequest = SignupRequest(
        userName = email,
        password = password,
        userAttributes = listOf(
            UserAttribute("name", name),
            UserAttribute("email", email),
            UserAttribute("picture", "none"),
            UserAttribute("updated_at", (System.currentTimeMillis() / 1000).toString()),
        )
    )
    registerScreenViewModel.signupAccount(signupAccountRequest)
}