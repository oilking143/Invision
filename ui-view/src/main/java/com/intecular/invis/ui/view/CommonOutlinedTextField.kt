package com.intecular.invis.ui.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.intecular.invis.common.ui.resource.R

@Composable
fun CommonOutlinedTextField(
    label: String,
    supportingText: String = "", // Make supportingText optional with a default value
    inputValue: MutableState<String>,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = inputValue.value,
        onValueChange = { inputValue.value = it },
        label = { Text(text = label) },
        supportingText = {
            if (supportingText.isNotEmpty()) {
                Text(text = supportingText)
            }
        },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { inputValue.value = "" }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete_content),
                    contentDescription = "Delete content icon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, capitalization = if (keyboardType == KeyboardType.Text) androidx.compose.ui.text.input.KeyboardCapitalization.Sentences else androidx.compose.ui.text.input.KeyboardCapitalization.None),
        visualTransformation = if (keyboardType == KeyboardType.Password) PasswordVisualTransformation() else VisualTransformation.None
    )
}