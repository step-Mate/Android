package jinproject.stepwalk.home.screen.component.tab.menu

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.HomeUiState
import jinproject.stepwalk.home.screen.HomeUiStatePreviewParameters
import jinproject.stepwalk.home.screen.state.HealthTab
import jinproject.stepwalk.home.screen.state.StepTabFactory
import jinproject.stepwalk.home.screen.state.toAchievementDegree
import java.text.DecimalFormat
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun MenuPager(
    healthTab: HealthTab,
    configuration: Configuration = LocalConfiguration.current
) {

    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2) {
        Integer.MAX_VALUE
    }
    val pages = healthTab.menu
    val goals = StepTabFactory.getMenuList(healthTab.header.goal.toLong())

    HorizontalPager(
        state = pagerState,
        pageSize = PageSize.Fixed(configuration.screenWidthDp.div(2).dp),
        contentPadding = PaddingValues(horizontal = configuration.screenWidthDp.div(4.5).dp)
    ) { page ->
        Card(
            modifier = Modifier
                .size(configuration.screenWidthDp.div(2).dp)
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
                    progress = progress,
                    modifier = Modifier
                        .fillMaxSize(),
                    strokeWidth = 4.dp,
                    color = progress.toAchievementDegree().toColor(),
                    trackColor = MaterialTheme.colorScheme.onSurfaceVariant
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

@Composable
@Preview
private fun PreviewMenuPager(
    @PreviewParameter(HomeUiStatePreviewParameters::class, 1)
    homeUiState: HomeUiState
) = StepWalkTheme {
    MenuPager(
        healthTab = homeUiState.step,
    )
}