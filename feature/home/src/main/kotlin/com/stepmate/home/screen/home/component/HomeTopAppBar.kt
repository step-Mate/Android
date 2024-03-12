package com.stepmate.home.screen.home.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stepmate.design.PreviewStepMateTheme
import com.stepmate.design.component.ColumnScopeTopBar
import com.stepmate.design.component.DefaultIconButton
import com.stepmate.design.component.HorizontalWeightSpacer

@Composable
internal fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    onClickTime: () -> Unit,
    onClickSetting: () -> Unit,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    ColumnScopeTopBar(
        modifier = modifier
            .height(200.dp)
            .fillMaxWidth(),
        icon = com.stepmate.design.R.drawable.ic_arrow_down_small,
        onClick = onClickTime,
        headerContent = {
            HorizontalWeightSpacer(float = 1f)
            DefaultIconButton(
                icon = com.stepmate.design.R.drawable.ic_setting,
                onClick = onClickSetting,
                iconTint = MaterialTheme.colorScheme.onSurface
            )
        },
        bodyContent = content
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeTopAppBar() =
    PreviewStepMateTheme {
        HomeTopAppBar(
            modifier = Modifier,
            onClickTime = {},
            onClickSetting = {},
        )
    }