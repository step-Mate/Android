package com.beank.profile.screen.profile.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DefaultTextButton

@Composable
internal fun ProfileButton(
    text: String,
    onClick: () -> Unit
) {
    DefaultTextButton(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        textPaddingValues = PaddingValues(horizontal = 24.dp, vertical = 10.dp),
        onClick = onClick
    )
}