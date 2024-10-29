package com.intecular.invis.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.intecular.invis.common.ui.resource.theme.Typography
import com.intecular.invis.ui.view.CommonTopAppBar
import com.intecular.invis.common.ui.resource.R
import com.intecular.invis.data.navigation.Screen

@ExperimentalMaterial3Api
@Composable
fun UserInfoScreen(navHostController: NavHostController, userName: String) {
    Scaffold(
        topBar = {
            CommonTopAppBar(titleText = userName) {
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
            val showSignOutReminderDialog = remember {
                mutableStateOf(false)
            }
            val showDeleteAccountDialog = remember {
                mutableStateOf(false)
            }
            val userInfoViewModel: UserInfoViewModel = hiltViewModel()
            UserInfoScreenContent(
                changeNameClicked = {
                    navHostController.navigate(Screen.ChangeNameScreen.route)
                },
                changePasswordClicked = {
                    navHostController.navigate(Screen.ChangePasswordScreen.route)
                },
                signOutClicked = {
                    showSignOutReminderDialog.value = true
                },
                deleteAccountClicked = {
                    showDeleteAccountDialog.value = true
                }
            )
            if (showSignOutReminderDialog.value) {
                ReminderDialog(
                    iconId = R.drawable.ic_log_out,
                    title = stringResource(id = R.string.do_you_want_to_sign_out),
                    firstRowContent = stringResource(id = R.string.do_you_really_want_to_sign_out_your_account),
                    secondRowContent = stringResource(id = R.string.your_account_information_will_not_be_lost),
                    positiveButtonText = stringResource(id = R.string.sign_out),
                    isSignOutConfirm = true,
                    confirmButtonClicked = {
                        userInfoViewModel.logOut()
                        showSignOutReminderDialog.value = false

                    },
                    dismissButtonClicked = {
                        showSignOutReminderDialog.value = false

                    }
                )
            }

            if (showDeleteAccountDialog.value) {
                ReminderDialog(
                    iconId = R.drawable.ic_delete_account,
                    title = stringResource(id = R.string.do_you_want_to_delete_account),
                    firstRowContent = stringResource(id = R.string.delete_account_will_unlink_your_devices),
                    secondRowContent = stringResource(id = R.string.will_not_be_unpaired_and_still_can_be_controlled),
                    positiveButtonText = stringResource(id = R.string.delete_account),
                    confirmButtonClicked = {
                        userInfoViewModel.deleteAccount()
                        showDeleteAccountDialog.value = false
                    },
                    dismissButtonClicked = {
                        showDeleteAccountDialog.value = false

                    }

                )
            }
            Box(Modifier.align(Alignment.Center)) {
                ApiLoading(userInfoViewModel)
            }
            UserInfoObserve(userInfoViewModel, navHostController)
            UserInfoErrorDialog(userInfoViewModel)
        }
    }
}

@Composable
fun ApiLoading(userInfoViewModel: UserInfoViewModel) {
    val showLoading = userInfoViewModel.showLoading.collectAsState(initial = false)
    if (showLoading.value) {
        CircularProgressIndicator()
    }
}


@Composable
fun UserInfoObserve(userInfoViewModel: UserInfoViewModel, navHostController: NavHostController) {
    val logoutSuccess = userInfoViewModel.logOutSuccess.collectAsState(initial = false)
    if (logoutSuccess.value) {
        LaunchedEffect(Unit) {
            navHostController.navigate(Screen.SignInScreen.route)
        }
    }
}

@Composable
fun UserInfoScreenContent(
    changeNameClicked: () -> Unit,
    changePasswordClicked: () -> Unit,
    signOutClicked: () -> Unit,
    deleteAccountClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onPrimary)
            .fillMaxSize()
            .padding(20.dp, 20.dp, 20.dp, 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Column(
            Modifier
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(20.dp)
                )
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null
                ) {

                }
        ) {
            UserInfoItem(
                content = stringResource(id = R.string.change_name),
                iconId = R.drawable.ic_person,
                itemClicked = changeNameClicked
            )
            UserInfoItem(
                content = stringResource(id = R.string.change_password),
                iconId = R.drawable.ic_change_password,
                itemClicked = changePasswordClicked
            )
        }

        Column(
            Modifier
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(20.dp, 20.dp, 0.dp, 0.dp)
                )
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null
                ) {

                }
        ) {
            UserInfoItem(
                content = stringResource(id = R.string.sign_out),
                iconId = R.drawable.ic_log_out,
                itemClicked = signOutClicked
            )
            Box(
                Modifier.background(
                    MaterialTheme.colorScheme.errorContainer,
                    RoundedCornerShape(0.dp, 0.dp, 20.dp, 20.dp)
                )
            ) {
                UserInfoItem(
                    content = stringResource(id = R.string.delete_account),
                    iconId = R.drawable.ic_delete_account,
                    itemClicked = deleteAccountClicked,
                    true
                )
            }

        }
    }
}

@Composable
fun UserInfoItem(
    content: String,
    iconId: Int,
    itemClicked: () -> Unit,
    isDeleteAccountItem: Boolean = false
) {
    ConstraintLayout(
        Modifier
            .padding(5.dp, 16.dp)
            .fillMaxWidth()
            .clickable(
                remember {
                    MutableInteractionSource()
                },
                null
            ) { itemClicked() },
    ) {
        val (itemIcon, text, arrow) = createRefs()
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = "User info item icon",
            modifier = Modifier.constrainAs(itemIcon) {
                start.linkTo(parent.start, margin = 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            tint = if (isDeleteAccountItem) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = content,
            modifier = Modifier.constrainAs(text) {
                start.linkTo(itemIcon.end, margin = 10.dp)
            },
            fontSize = 16.sp,
            fontWeight = FontWeight.W400,
            style = Typography.bodyLarge
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_right_black_arrow),
            contentDescription = "Next step arrow icon",
            modifier = Modifier.constrainAs(arrow) {
                end.linkTo(parent.end, margin = 10.dp)
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
            },
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ReminderDialog(
    iconId: Int,
    title: String,
    firstRowContent: String,
    secondRowContent: String,
    positiveButtonText: String,
    isSignOutConfirm: Boolean = false,
    confirmButtonClicked: () -> Unit,
    dismissButtonClicked: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            Button(
                onClick = confirmButtonClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text(
                    text = positiveButtonText,
                    fontWeight = FontWeight.W500,
                    style = Typography.labelLarge,
                    fontSize = 14.sp,
                    color = if (isSignOutConfirm) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            Button(
                onClick = dismissButtonClicked,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    fontWeight = FontWeight.W500,
                    style = Typography.labelLarge,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        icon = {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = "Reminder dialog icon",
                tint = if (isSignOutConfirm) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onErrorContainer
            )
        },
        title = {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.W400,
                style = Typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column(
                verticalArrangement = if (isSignOutConfirm) Arrangement.spacedBy(10.dp) else Arrangement.spacedBy(
                    0.dp
                )
            ) {
                Text(
                    text = firstRowContent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = secondRowContent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    style = Typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

        },
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )
}

@Composable
fun UserInfoErrorDialog(userInfoViewModel: UserInfoViewModel) {
    val errorResponse =
        userInfoViewModel.errorResponse.collectAsState(initial = null)
    val showErrorDialog = remember {
        mutableStateOf(false)
    }
    val errorMessage = userInfoViewModel.errorMessage.collectAsState(initial = "")
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
                        userInfoViewModel.reductionErrorResponse()
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