package jinproject.stepwalk.home.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.R.drawable as AppIcon
import jinproject.stepwalk.design.R.string as AppText

@Composable
internal fun IdField(value: String, onNewValue: (String) -> Unit) {
    OutlinedTextField(
        singleLine = true,
        modifier = Modifier.fieldModifier(),
        value = value,
        textStyle = MaterialTheme.typography.bodyMedium,
        onValueChange = onNewValue,
        placeholder = { Text(stringResource(AppText.id), style = MaterialTheme.typography.bodyMedium) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Email") }
    )
}

@Composable
internal fun IdField(value: String, onNewValue: (String) -> Unit, isError : Boolean = false, enable : Boolean = true) {
    OutlinedTextField(
        singleLine = true,
        modifier = Modifier.fieldModifier(),
        value = value,
        enabled = enable,
        isError = isError,
        textStyle = MaterialTheme.typography.bodyMedium,
        onValueChange = onNewValue,
        placeholder = { Text(stringResource(AppText.id), style = MaterialTheme.typography.bodyMedium) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Email") },
    )
}

@Composable
internal fun PasswordField(value: String, onNewValue: (String) -> Unit, isError : Boolean = false) {
    PasswordField(value, AppText.password, onNewValue, isError = isError)
}

@Composable
internal fun RepeatPasswordField(value: String, onNewValue: (String) -> Unit, isError : Boolean = false) {
    PasswordField(value, AppText.repeat_password, onNewValue, isError = isError)
}

@Composable
private fun PasswordField(
    value: String,
    @StringRes placeholder: Int,
    onNewValue: (String) -> Unit,
    isError : Boolean = false
) {
    var isVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.fieldModifier(),
        value = value,
        onValueChange = onNewValue,
        textStyle = MaterialTheme.typography.bodyMedium,
        placeholder = { Text(text = stringResource(placeholder), style = MaterialTheme.typography.bodyMedium) },
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(imageVector =  if (isVisible) ImageVector.vectorResource(AppIcon.ic_visibility_on)
                else ImageVector.vectorResource(AppIcon.ic_visibility_off), contentDescription = "Visibility")
            }
        },
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}

fun Modifier.fieldModifier(): Modifier {
    return this
        .fillMaxWidth()
        .padding(12.dp, 4.dp)
}