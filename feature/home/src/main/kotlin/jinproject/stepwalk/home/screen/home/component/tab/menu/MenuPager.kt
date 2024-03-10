package jinproject.stepwalk.home.screen.home.component.tab.menu

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.window.core.layout.WindowWidthSizeClass
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.home.HomeUiState
import jinproject.stepwalk.home.screen.home.HomeUiStatePreviewParameters
import jinproject.stepwalk.home.screen.home.state.HealthTab
import jinproject.stepwalk.home.screen.home.state.StepTabFactory
import jinproject.stepwalk.home.screen.home.state.toAchievementDegree
import java.text.DecimalFormat
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun MenuPager(
    healthTab: HealthTab,
) {
    val pageNumber = remember {
        100
    }

    val pagerState = rememberPagerState(initialPage = pageNumber / 2) {
        pageNumber
    }

    val pages = healthTab.menu
    val goals = StepTabFactory.getMenuList(healthTab.header.goal.toLong())

    BoxWithConstraints(modifier = Modifier) {
        val pageSize: Dp
        val cardSize: Dp
        val contentPadding: Dp

        when (currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass) {
            WindowWidthSizeClass.EXPANDED, WindowWidthSizeClass.MEDIUM -> {
                cardSize = maxWidth.value.div(4f).dp
                pageSize = maxWidth.value.div(3f).dp
                contentPadding = maxWidth.value.div(2.65f).dp
            }

            else -> {
                cardSize = maxWidth.value.div(2f).dp
                pageSize = maxWidth.value.div(2f).dp
                contentPadding = maxWidth.value.div(4f).dp
            }
        }

        HorizontalPager(
            state = pagerState,
            pageSize = PageSize.Fixed(pageSize),
            contentPadding = PaddingValues(horizontal = contentPadding)
        ) { page ->
            Card(
                modifier = Modifier
                    .size(cardSize)
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
                            start = 0.7f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        scaleY = lerp(
                            start = 0.7f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                        shape = CircleShape
                        clip = true
                        shadowElevation = 30f
                    },

                ) {
                Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                    val currentPage = pages[page % pages.size]

                    val figure = when (currentPage.intro.contains("분")) {
                        true -> currentPage.value.toInt().toFloat()
                        false -> String.format("%.2f", currentPage.value).toFloat()
                    }

                    val goal = goals.first { it.intro == currentPage.intro }.value

                    val progress = figure / goal

                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        color = progress.toAchievementDegree().toColor(),
                        strokeWidth = 4.dp,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        strokeCap = StrokeCap.Round,
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = DecimalFormat("#,###").format(currentPage.value),
                            style = MaterialTheme.typography.headlineLarge,
                            color = progress.toAchievementDegree().toColor()
                        )
                        VerticalSpacer(height = 4.dp)
                        Text(
                            text = currentPage.intro,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewMenuPager(
    @PreviewParameter(HomeUiStatePreviewParameters::class, 1)
    homeUiState: HomeUiState,
) = StepWalkTheme {
    MenuPager(
        healthTab = homeUiState.step,
    )
}