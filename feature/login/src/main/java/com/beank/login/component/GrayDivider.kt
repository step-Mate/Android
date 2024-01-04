package com.beank.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun GrayVerticalDivider(
    modifier : Modifier
){
    Spacer(
        modifier = modifier
            .width(1.dp)
            .background(MaterialTheme.colorScheme.scrim)
    )
}

@Composable
internal fun GrayHorizontalDivider(
    modifier : Modifier
){
    Spacer(
        modifier = modifier
            .height(1.dp)
            .background(MaterialTheme.colorScheme.scrim)
    )
}