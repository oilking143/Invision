package com.intecular.invis.signin.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.intecular.invis.common.ui.resource.R

@Composable
fun DefaultVerifyPasswordMessage(
    verifyColor: MutableState<Color>,
    verifyMessage: MutableState<String>,
) {
    verifyColor.value = MaterialTheme.colorScheme.onSurfaceVariant
    verifyMessage.value = stringResource(id = R.string.type_new_password_again)
}