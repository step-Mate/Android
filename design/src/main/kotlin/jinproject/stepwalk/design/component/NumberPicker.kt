package jinproject.stepwalk.design.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import jinproject.stepwalk.design.theme.StepWalkTheme
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun StepMateNumberPicker(
    isSheetVisible: Boolean,
    items: List<Int>,
    pagerState: PagerState,
    sheetState: SheetState,
    updateIsSheetVisibility: (Boolean) -> Unit,
    onDismissSheet: () -> Unit,
) {
    if (isSheetVisible)
        ModalBottomSheet(
            onDismissRequest = {
                onDismissSheet()
                updateIsSheetVisibility(false)
            },
            sheetState = sheetState,
            dragHandle = {
                BottomSheetDefaults.DragHandle()
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            VerticalPager(
                modifier = Modifier.height(300.dp),
                state = pagerState,
                pageSize = PageSize.Fixed(100.dp),
                contentPadding = PaddingValues(vertical = 100.dp)
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .padding(10.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    HeadlineText(
                        text = items[page].toString(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                            .graphicsLayer {
                                val pageOffset = (
                                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                                        ).absoluteValue
                                alpha = lerp(
                                    start = 0.3f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                                scaleX = lerp(
                                    start = 0.5f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                                scaleY = lerp(
                                    start = 0.5f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                            },
                    )
                }
            }
        }
}

@OptIn(
    ExperimentalFoundationApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
@Preview
private fun PreviewStepMateNumberPicker() = StepWalkTheme {
    StepMateNumberPicker(
        isSheetVisible = true,
        items = (0..50000 step 100).toList(),
        pagerState = rememberPagerState(50) {
            50000 / 100 + 1
        },
        sheetState = rememberModalBottomSheetState(),
        updateIsSheetVisibility = {}
    ) {

    }
}