package jinproject.stepwalk.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import kotlin.math.roundToInt

@Composable
fun DefaultLayout(
    modifier: Modifier = Modifier,
    contentPaddingValues: PaddingValues,
    topBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        topBar()
        VerticalSpacer(height = 8.dp)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPaddingValues)
        ) {
            content()
        }
    }
}

@Composable
fun HideableTopBarLayout(
    modifier: Modifier = Modifier,
    systemBarHidingState: SystemBarHidingState,
    topBar: @Composable (Modifier) -> Unit,
    content: @Composable (Modifier) -> Unit,
) {
    Layout(
        content = {
            topBar(
                Modifier
                    .layoutId("topBar")
            )
            content(
                Modifier
                    .layoutId("content")
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            )
        },
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .topBarHidingScroll(systemBarHidingState),
    ) { measurables, constraints ->
        val looseConstraints = constraints.asLoose()
        val maxHeight = constraints.maxHeight
        val topBarHeightByOffset = systemBarHidingState.offset.roundToInt()

        val topBarPlaceable =
            measurables.fastFirstOrNull { it.layoutId == "topBar" }?.measure(
                looseConstraints.copy(
                    maxHeight = systemBarHidingState.bar.maxHeight + topBarHeightByOffset,
                )
            )

        val contentPlaceable =
            measurables.fastFirstOrNull { it.layoutId == "content" }?.measure(
                looseConstraints.copy(
                    maxHeight = maxHeight - (topBarPlaceable?.height
                        ?: 0)
                )
            )

        layout(constraints.maxWidth, constraints.maxHeight) {
            topBarPlaceable?.place(0, 0)
            contentPlaceable?.place(0, (topBarPlaceable?.height ?: 0))
        }

    }
}

fun Constraints.asLoose() = this.copy(
    minWidth = 0,
    minHeight = 0
)