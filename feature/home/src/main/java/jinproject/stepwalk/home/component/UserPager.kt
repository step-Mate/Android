package jinproject.stepwalk.home.component

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
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
import jinproject.stepwalk.domain.model.METs
import jinproject.stepwalk.home.HomeUiState
import jinproject.stepwalk.home.User
import jinproject.stepwalk.home.state.HealthState
import jinproject.stepwalk.home.state.HeartRateMenu
import jinproject.stepwalk.home.state.MenuDetail
import jinproject.stepwalk.home.state.Step
import jinproject.stepwalk.home.state.StepMenu
import jinproject.stepwalk.home.state.Time
import jinproject.stepwalk.home.state.sortDayOfWeek
import jinproject.stepwalk.home.state.toAchievementDegree
import jinproject.stepwalk.home.state.weekToString
import java.text.DecimalFormat
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun UserPager(
    uiState: HomeUiState,
    stepThisHour: Int,
    selectedStepOnGraph: Long,
    modifier: Modifier = Modifier,
    setSelectedStepOnGraph: (Long) -> Unit
) {
    val pages = uiState.toHealthStateList(stepThisHour)

    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2) {
        Integer.MAX_VALUE
    }

    PageMenu(
        pages = pages,
        pagerState = pagerState,
        modifier = modifier,
        selectedStepOnGraph = selectedStepOnGraph,
        setSelectedStepOnGraph = setSelectedStepOnGraph
    )

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PageMenu(
    modifier: Modifier = Modifier,
    pages: List<HealthState>,
    pagerState: PagerState,
    selectedStepOnGraph: Long,
    setSelectedStepOnGraph: (Long) -> Unit
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

        val graphItems = menu.graphItems
        val graphHorizontalItems = (0L until (graphItems?.size?.toLong() ?: 0L)).toList()
        val graphVerticalMax = graphItems?.maxOrNull() ?: 0
        val popUpState = remember { mutableStateOf(false) }
        val popUpOffset = remember {
            mutableStateOf(Offset(0F, 0F))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.secondary)
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp, top = 10.dp)
        ) {
            PagerGraph(
                itemsCount = graphHorizontalItems.size,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(10.dp),
                horizontalAxis = { index ->
                    StepGraphTail(
                        item = when (graphHorizontalItems.size) {
                            Time.Week.toRepeatTimes() -> graphHorizontalItems
                                .sortDayOfWeek()[index]
                                .weekToString()

                            Time.Day.toRepeatTimes() -> graphHorizontalItems[index].toString()
                            else -> (graphHorizontalItems[index] + 1).toString()
                        }
                    )
                },
                verticalAxis = {
                    StepGraphHeader(
                        max = graphVerticalMax.toString(),
                        avg = (graphVerticalMax / 2).toString()
                    )
                },
                bar = { index ->
                    StepBar(
                        index = index,
                        item = graphItems?.get(index) ?: 0L,
                        nextItem = kotlin.runCatching { graphItems?.get(index + 1) ?: 0L }
                            .getOrDefault(0L),
                        maxItem = graphVerticalMax,
                        horizontalSize = graphHorizontalItems.size,
                        setSelectedStepOnGraph = { step -> setSelectedStepOnGraph(step) },
                        setPopUpState = { popUpState.value = true },
                        setPopUpOffset = { offset -> popUpOffset.value = offset }
                    )
                }
            )

            PopupWindow(
                value = selectedStepOnGraph,
                popUpState = popUpState.value,
                popUpOffset = popUpOffset.value,
                offPopUp = { popUpState.value = false }
            )
        }
    }
}

@Composable
private fun StepBar(
    index: Int,
    item: Long,
    nextItem: Long,
    maxItem: Long,
    horizontalSize: Int,
    modifier: Modifier = Modifier,
    setSelectedStepOnGraph: (Long) -> Unit,
    setPopUpState: () -> Unit,
    setPopUpOffset: (Offset) -> Unit,
) {
    val clickState = remember {
        mutableStateOf(false)
    }
    val height = rememberSaveable {
        mutableFloatStateOf(0f)
    }
    Spacer(
        modifier = modifier
            .clickable {
                setSelectedStepOnGraph(item)
                setPopUpState()
                clickState.value = true
            }
            .onGloballyPositioned {
                if (clickState.value) {
                    setPopUpOffset(
                        Offset(
                            x = it.positionInWindow().x,
                            y = it.positionInWindow().y + height.floatValue
                        )
                    )
                    clickState.value = false
                }
            }
            .drawWithCache {
                val stroke = Stroke(2.dp.toPx())
                val brush = Brush.verticalGradient(
                    colors = listOf(
                        StepWalkColor.blue_700.color,
                        StepWalkColor.blue_600.color,
                        StepWalkColor.blue_500.color,
                        StepWalkColor.blue_400.color,
                        StepWalkColor.blue_300.color
                    )
                )
                val fillBrush = Brush.verticalGradient(
                    colors = listOf(
                        StepWalkColor.blue_700.color.copy(alpha = 0.8f),
                        StepWalkColor.blue_400.color.copy(alpha = 0.6f),
                        Color.Transparent
                    )
                )

                height.floatValue = this@drawWithCache.size.height - item.stepToSizeByMax(
                    barHeight = this@drawWithCache.size.height,
                    max = maxItem
                )

                val path = Path().apply {
                    moveTo(
                        x = 10f,
                        y = this@drawWithCache.size.height - item.stepToSizeByMax(
                            barHeight = this@drawWithCache.size.height,
                            max = maxItem
                        )
                    )
                    if (index < horizontalSize - 1) {
                        lineTo(
                            x = this@drawWithCache.size.width + 10f,
                            y = this@drawWithCache.size.height - nextItem.stepToSizeByMax(
                                barHeight = this@drawWithCache.size.height,
                                max = maxItem
                            )
                        )
                    }
                }

                val filledPath = Path().apply {
                    addPath(path)
                    if (index < horizontalSize - 1) {
                        lineTo(
                            x = this@drawWithCache.size.width + 10f,
                            y = this@drawWithCache.size.height
                        )
                        lineTo(
                            x = 10f,
                            y = this@drawWithCache.size.height
                        )
                    }
                    close()
                }

                onDrawBehind {
                    drawPath(path, brush, style = stroke)
                    drawPath(filledPath, fillBrush, style = Fill)
                    drawCircle(
                        brush,
                        radius = 10f,
                        center = Offset(
                            x = 10f,
                            y = this.size.height - item.stepToSizeByMax(
                                barHeight = this@drawWithCache.size.height,
                                max = maxItem
                            )
                        )
                    )
                }
            }
    )
}

@Composable
private fun StepGraphTail(
    item: String
) {
    Text(
        text = item,
        textAlign = TextAlign.Left,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun StepGraphHeader(
    max: String,
    avg: String
) {
    Column {
        Text(
            text = max,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = avg,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
    }
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
    details: Map<String, MenuDetail>?,
    configuration: Configuration = LocalConfiguration.current
) {
    val menuList = details?.toList() ?: emptyList()
    val menuCounter = details?.size ?: 3
    val cardSpacerSize = (menuCounter - 1) * 24
    val cardSize = (configuration.screenWidthDp - cardSpacerSize - 16) / menuCounter

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
                        text = when (item.second.intro.contains("분")) {
                            true -> item.second.value.toInt().toString()
                            false -> String.format("%.2f", item.second.value)
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
fun PreviewUserStepsByHour() = PreviewStepWalkTheme {
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
                    ),
                    Step(
                        distance = 800,
                        start = 60,
                        end = 80,
                        type = METs.Walk
                    ),
                    Step(
                        distance = 800,
                        start = 60,
                        end = 80,
                        type = METs.Walk
                    ),
                    Step(
                        distance = 800,
                        start = 60,
                        end = 80,
                        type = METs.Walk
                    ),
                    Step(
                        distance = 800,
                        start = 60,
                        end = 80,
                        type = METs.Walk
                    )
                )
            ).apply {
                setMenuDetails(55f)
                setGraphItems(Time.Week)
            },
            user = User.getInitValues(),
            heartRate = HeartRateMenu.getInitValues(),
            time = Time.Week
        ),
        stepThisHour = 100,
        selectedStepOnGraph = 0L,
        setSelectedStepOnGraph = {}
    )
}

@Composable
@Preview
fun PreviewUserStepsByWeek() = PreviewStepWalkTheme {
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
            ).apply {
                setMenuDetails(55f)
                setGraphItems(Time.Day)
            },
            user = User.getInitValues(),
            heartRate = HeartRateMenu.getInitValues(),
            time = Time.Day
        ),
        stepThisHour = 800,
        selectedStepOnGraph = 0L,
        setSelectedStepOnGraph = {}
    )
}