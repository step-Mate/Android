package com.beank.profile.screen.terms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.component.StepMateBoxDefaultTopBar

@Composable
internal fun TermsScreen(
    termsViewModel: TermsViewModel = hiltViewModel(),
    popBackStack: () -> Unit
) {

    TermsScreen(
        popBackStack = popBackStack
    )
}

@Composable
private fun TermsScreen(
    popBackStack: () -> Unit
) {
    DefaultLayout(
        contentPaddingValues = PaddingValues(),
        topBar = {
            StepMateBoxDefaultTopBar(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.statusBars),
                icon = R.drawable.ic_arrow_left_small,
                onClick = popBackStack
            ) {
                HeadlineText(text = "이용 약관", modifier = Modifier.align(Alignment.Center))
            }
        }
    ) {
        DescriptionSmallText(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            text = ""
        )
    }
}