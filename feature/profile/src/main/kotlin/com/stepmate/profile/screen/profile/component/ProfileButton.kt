package com.stepmate.profile.screen.profile.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stepmate.design.R
import com.stepmate.design.component.DefaultTextButton

@Composable
internal fun ProfileButton(
    text: String,
    onClick: () -> Unit
) {
    DefaultTextButton(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        textPaddingValues = PaddingValues(horizontal = 30.dp, vertical = 10.dp),
        onClick = onClick
    )
}

@Composable
internal fun ProfileEnterButton(
    text: String,
    onClick: () -> Unit
) {
    DefaultTextButton(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
        textPaddingValues = PaddingValues(horizontal = 30.dp, vertical = 10.dp),
        onClick = onClick,
        content = {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right_small),
                contentDescription = "arrow right",
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(40.dp)
                    .padding(end = 15.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    )
}