package com.beank.profile.screen.terms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.beank.profile.screen.terms.component.TermsWebView
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.component.StepMateBoxDefaultTopBar

@Composable
internal fun TermsScreen(
    popBackStack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        StepMateBoxDefaultTopBar(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .windowInsetsPadding(WindowInsets.statusBars),
            icon = R.drawable.ic_arrow_left_small,
            onClick = popBackStack
        ) {
            HeadlineText(text = "이용 약관", modifier = Modifier.align(Alignment.Center))
        }
        TermsWebView(url = "https://sites.google.com/view/stepmate-personal-info-policy/%ED%99%88")
    }
}
