package jinproject.stepwalk.home.component

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import jinproject.stepwalk.design.PreviewStepWalkTheme
import jinproject.stepwalk.design.component.HorizontalSpacer
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.domain.METs
import jinproject.stepwalk.home.HomeUiState
import jinproject.stepwalk.home.User
import jinproject.stepwalk.home.component.GraphScope.setGraphParentData
import jinproject.stepwalk.home.state.HealthState
import jinproject.stepwalk.home.state.MenuDetail
import jinproject.stepwalk.home.state.Step
import jinproject.stepwalk.home.state.StepMenu
import jinproject.stepwalk.home.state.toAchievementDegree
import java.text.DecimalFormat
import java.util.SortedMap
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun UserPager(
    uiState: HomeUiState,
    modifier: Modifier = Modifier
) {
    val pages = uiState.toHealthStateList()

    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2) {
        Integer.MAX_VALUE
    }

    PageMenu(
        pages = pages,
        pagerState = pagerState,
        modifier = modifier
    )

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PageMenu(
    modifier: Modifier = Modifier,
    pages: List<HealthState>,
    pagerState: PagerState
) {
    val currentPage = pages[pagerState.currentPage % pages.size].type
    val menu = currentPage.menu

    Column(
        modifier = modifier
    ) {
        MenuPager(
            pages = pages,
            pagerState = pagerState
        )

        VerticalSpacer(height = 20.dp)

        MenuDetails(
            details = menu.details
        )

        VerticalSpacer(height = 40.dp)

        val graphItems = currentPage.menu.graphItems

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.secondary)
                .padding(start = 30.dp, end = 30.dp, bottom = 30.dp, top = 50.dp)
        ) {
            PagerGraph(
                itemsCount = graphItems.keys.size,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 10.dp),
                horizontalAxis = { index ->
                    StepGraphTail(
                        item = graphItems.keys.toList()[index]
                    )
                },
                verticalAxis = {
                    StepGraphHeader(
                        max = graphItems.values.max().toString(),
                        avg = graphItems.values.average().toString()
                    )
                },
                bar = { index ->
                    StepBar(
                        item = menu.graphItems,
                        key = index,
                        goal = pages[pagerState.currentPage % pages.size].max.toLong(),
                        modifier = Modifier
                    )
                }
            )
        }
    }
}

@Composable
private fun StepBar(
    item: SortedMap<Int, Long>,
    key: Int,
    goal: Long,
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier
            .setGraphParentData(
                data = item,
                key = key,
                goal = goal
            )
            .drawBehind {
                /*
                drawLine(
                    color = StepWalkColor.blue.color,
                    start = Offset(0f, 0f),
                    end = Offset(0f, -(step.stepToGraphSize())),
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
                 */
                val brush = Brush.verticalGradient(
                    colors = listOf(
                        StepWalkColor.blue_300.color,
                        StepWalkColor.blue_400.color,
                        StepWalkColor.blue_500.color,
                        StepWalkColor.blue_600.color,
                        StepWalkColor.blue_700.color,
                        StepWalkColor.blue_800.color,
                        StepWalkColor.blue_900.color
                    )
                )
                drawRoundRect(
                    brush = brush,
                    cornerRadius = CornerRadius(10f, 10f)
                )
            }
            .width(6.dp)
    )
}

@Composable
private fun StepGraphTail(
    item: Int
) {
    Text(
        text = item.toString(),
        textAlign = TextAlign.Left,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.size(24.dp)
    )
}

@Composable
private fun StepGraphHeader(
    max: String,
    avg: String
) {
    Text(
        text = max,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MenuPager(
    pages: List<HealthState>,
    pagerState: PagerState,
    configuration: Configuration = LocalConfiguration.current
) {
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
                    horizontalAlignment = CenterHorizontally
                ) {
                    Text(
                        text = DecimalFormat("#,###").format(currentPage.figure),
                        style = MaterialTheme.typography.headlineLarge,
                        color = progress.toAchievementDegree().toColor()
                    )
                    VerticalSpacer(height = 4.dp)
                    Text(
                        text = currentPage.type.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuDetails(
    details: Map<String, MenuDetail>,
    configuration: Configuration = LocalConfiguration.current
) {
    val menuList = details.toList()
    val cardSpacerSize = (details.size - 1) * 24
    val cardSize = (configuration.screenWidthDp - cardSpacerSize - 16) / details.size

    Row(modifier = Modifier.fillMaxWidth()) {
        menuList.forEachIndexed { index, item ->
            Surface(
                modifier = Modifier.size(cardSize.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondary,
                tonalElevation = 10.dp,
                shadowElevation = 10.dp
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = item.second.img),
                        contentDescription = "IconDetail"
                    )
                    Text(
                        text = when (item.first) {
                            "minutes" -> item.second.value.toInt().toString()
                            else -> String.format("%.2f", item.second.value)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = item.second.intro,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (index != menuList.lastIndex)
                HorizontalSpacer(width = 24.dp)
        }
    }
}

@Composable
@Preview
fun PreviewUserSteps() = PreviewStepWalkTheme {
    UserPager(
        uiState = HomeUiState(
            step = StepMenu(
                steps = listOf(
                    Step(
                        distance = 2000,
                        start = 0,
                        end = 1,
                        type = METs.Walk
                    ),
                    Step(
                        distance = 500,
                        start = 30,
                        end = 50,
                        type = METs.Walk
                    ),
                    Step(
                        distance = 800,
                        start = 60,
                        end = 80,
                        type = METs.Walk
                    )
                )
            ),
            user = User.getInitValues()
        )
    )
}