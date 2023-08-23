package jinproject.stepwalk.app.ui.home.component

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import jinproject.stepwalk.app.ui.home.state.HealthState
import jinproject.stepwalk.app.ui.home.utils.toAchievementDegree
import jinproject.stepwalk.design.PreviewStepWalkTheme
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.Typography
import java.text.DecimalFormat
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserPager(
    pages: List<HealthState>,
    configuration: Configuration = LocalConfiguration.current
) {
    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2 - 1) {
        Integer.MAX_VALUE
    }

    HorizontalPager(
        state = pagerState,
        pageSize = PageSize.Fixed(configuration.screenWidthDp.div(2).dp),
        contentPadding = PaddingValues(horizontal = 100.dp),
        pageSpacing = 20.dp
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
                }
                .clip(CircleShape),

            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Box(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                val currentPage = pages[ page % pages.size]
                val progress = currentPage.figure.toFloat() / currentPage.max.toFloat()

                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxSize(),
                    strokeWidth = 4.dp,
                    color = progress.toAchievementDegree().toColor(),
                    trackColor = MaterialTheme.colorScheme.onSurface
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = DecimalFormat("#,###").format(currentPage.figure),
                        style = Typography.headlineLarge,
                        color = progress.toAchievementDegree().toColor()
                    )
                    VerticalSpacer(height = 4.dp)
                    Text(
                        text = currentPage.name,
                        style = Typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewUserSteps() = PreviewStepWalkTheme {
    UserPager(
        pages = listOf(
            HealthState(
                name = "걷기",
                figure = 2000,
                max = 5000
            ),
            HealthState(
                name = "심박수",
                figure = 100,
                max = 200
            ),
            HealthState(
                name = "물 섭취량",
                figure = 2200,
                max = 2000
            ),
            HealthState(
                name = "산소포화도",
                figure = 10,
                max = 100
            )
        )
    )
}