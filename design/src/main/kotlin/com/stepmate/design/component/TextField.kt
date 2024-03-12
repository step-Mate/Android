package com.stepmate.design.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.stepmate.design.theme.StepMateTheme

@Composable
fun DefaultOutlinedTextField(
    modifier: Modifier = Modifier,
    informationText: String,
    errorMessage: String = "",
    value: String,
    isError: Boolean = false,
    enabled: Boolean = true,
    errorColor: Color = MaterialTheme.colorScheme.error,
    keyboardType: KeyboardType = KeyboardType.Email,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onNewValue: (String) -> Unit
) {
    OutlinedTextField(
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        value = value,
        isError = isError,
        enabled = enabled,
        textStyle = MaterialTheme.typography.bodyMedium,
        onValueChange = onNewValue,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = { Text(informationText, style = MaterialTheme.typography.bodyMedium) },
        supportingText = {
            if (isError) {
                DescriptionSmallText(text = errorMessage, color = errorColor)
            }
        },
        suffix = suffix,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            errorContainerColor = MaterialTheme.colorScheme.background,
            errorIndicatorColor = errorColor,
            errorLabelColor = errorColor,
            errorSupportingTextColor = errorColor,
            errorTextColor = errorColor
        )
    )
}

@Composable
@Preview
private fun PreviewInformationField(

) = StepMateTheme {
    Column {
        DefaultOutlinedTextField(
            informationText = "몸무게",
            errorMessage = "잘못된 양식입니다.",
            value = "",
            isError = true,
            suffix = { DescriptionSmallText(text = "kg") },
            onNewValue = {}
        )
    }
}