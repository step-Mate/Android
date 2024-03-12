package com.stepmate.login.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stepmate.design.R
import com.stepmate.design.component.HeadlineText
import com.stepmate.design.component.StepMateBoxDefaultTopBar
import com.stepmate.design.component.layout.DefaultLayout

@Composable
internal fun LoginLayout(
    modifier: Modifier = Modifier,
    text: String,
    content: @Composable ColumnScope.() -> Unit,
    bottomContent: @Composable ColumnScope.() -> Unit,
    popBackStack: () -> Unit,
) {
    DefaultLayout(
        modifier = Modifier.fillMaxSize()
            .imePadding()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState()),
        topBar = {
            StepMateBoxDefaultTopBar(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.statusBars),
                icon = R.drawable.ic_arrow_left_small,
                onClick = popBackStack
            ) {
                HeadlineText(text = text, modifier = Modifier.align(Alignment.Center))
            }
        },
        contentPaddingValues = PaddingValues(horizontal = 12.dp)
    ) {
        Column(
            modifier = Modifier
        ) {
            content()
        }
        Column(
            modifier = modifier.padding(bottom = 10.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            bottomContent()
        }
    }
}