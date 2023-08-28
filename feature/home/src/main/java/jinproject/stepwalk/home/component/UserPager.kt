package jinproject.stepwalk.home.component

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import jinproject.stepwalk.design.PreviewStepWalkTheme
import jinproject.stepwalk.design.component.HorizontalSpacer
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.domain.METs
import jinproject.stepwalk.home.HomeUiState
import jinproject.stepwalk.home.MenuDetail
import jinproject.stepwalk.home.User
import jinproject.stepwalk.home.component.GraphScope.setGraphParentData
import jinproject.stepwalk.home.state.HealthState
import jinproject.stepwalk.home.state.Page
import jinproject.stepwalk.home.state.Step
import jinproject.stepwalk.home.state.getMenuDetails
import jinproject.stepwalk.home.state.toGraphItems
import jinproject.stepwalk.home.utils.toAchievementDegree
import java.text.DecimalFormat
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun UserPager(
    uiState: HomeUiState
) {
    val pages = mutableListOf(
        HealthState(
            type = Page.Step,
            figure = uiState.steps
                .map { it.distance }
                .reduce { acc, step -> acc + step }
                .toInt(),
            max = 5000
        ),
        HealthState(
            type = Page.HeartRate,
            figure = 100,
            max = 200
        ),
        HealthState(
            type = Page.DrinkWater,
            figure = 2500,
            max = 2000
        ),
    )

    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2) {
        Integer.MAX_VALUE
    }

    Column {
        MenuPager(
            pages = pages,
            pagerState = pagerState
        )

        VerticalSpacer(height = 20.dp)

        when (pages[pagerState.currentPage % pages.size].type) {
            Page.Step -> {
                MenuDetails(details = uiState.steps.getMenuDetails(55f))
            }

            Page.HeartRate -> {

            }

            Page.DrinkWater -> {

            }
        }

        VerticalSpacer(height = 20.dp)

        val scrollState = rememberScrollState()
        MenuDetailGraph(
            itemsCount = 24,
            modifier = Modifier
                .horizontalScroll(scrollState)
                .wrapContentSize(),
            horizontalAxis = {
                StepGraphTail()
            },
            verticalAxis = {
                StepGraphHeader()
            },
            bar = { index ->
                val item = uiState.steps.toGraphItems()

                StepBar(
                    step = item[index] ?: 0,
                    modifier = Modifier.setGraphParentData(value = item[index] ?: 0, maxValue = item.values.max())
                )
            }
        )
    }
}

@Composable
private fun StepBar(
    step: Long,
    modifier: Modifier = Modifier,
) {
    Spacer(
        modifier = modifier
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
            .width(16.dp)
    )
}

@Composable
private fun StepGraphTail() {
    Row(modifier = Modifier.height(24.dp)) {
        (0..23).forEach { hour ->
            Text(
                text = hour.toString(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.width(24.dp)
            )
        }
    }
}

@Composable
private fun StepGraphHeader() {
    Column {
        Text(text = "걸음 수")
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
                }
                .clip(CircleShape),

            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = DecimalFormat("#,###").format(currentPage.figure),
                        style = MaterialTheme.typography.headlineLarge,
                        color = progress.toAchievementDegree().toColor()
                    )
                    VerticalSpacer(height = 4.dp)
                    Text(
                        text = currentPage.type.display(),
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
fun MenuDetailGraph(
    itemsCount: Int,
    modifier: Modifier = Modifier,
    horizontalAxis: @Composable () -> Unit,
    verticalAxis: @Composable () -> Unit,
    bar: @Composable (Int) -> Unit
) {
    val bars = @Composable { repeat(itemsCount) { bar(it) } }

    Layout(
        contents = listOf(horizontalAxis, verticalAxis, bars),
        modifier = modifier
    ) { (horizontalMeasurables, verticalMeasurables, barMeasurables), constraints ->

        require(horizontalMeasurables.size == 1 && verticalMeasurables.size == 1) {
            "가로축과 세로축은 반드시 하나만 존재해야 함"
        }

        val horizontalPlacable = horizontalMeasurables.first().measure(constraints)
        val verticalPlacable = verticalMeasurables.first().measure(constraints)

        val totalWidth = horizontalPlacable.width + verticalPlacable.width

        val barPlacable = barMeasurables.map { barMeasurable ->
            val barParentData = barMeasurable.parentData as GraphParentData
            val barHeight = (barParentData.value).roundToInt()

            val barPlacable = barMeasurable.measure(
                constraints.copy(
                    minHeight = barHeight,
                    maxHeight = barHeight,
                )
            )

            barPlacable
        }

        val totalHeight = horizontalPlacable.height + barPlacable.maxOf { it.height }

        layout(totalWidth, totalHeight) {
            var xPos = verticalPlacable.width
            val yPos = verticalPlacable.height

            verticalPlacable.place(0, totalHeight - (yPos * 2))
            horizontalPlacable.place(xPos, totalHeight - yPos)

            barPlacable.forEach { placeable ->
                val barOffset = xPos + 4.dp.roundToPx()
                placeable.place(barOffset, totalHeight - yPos - placeable.height)
                xPos += placeable.width + 8.dp.roundToPx()
            }
        }
    }
}

@LayoutScopeMarker
@Immutable
object GraphScope {
    @Stable
    fun Modifier.setGraphParentData(
        value: Long,
        maxValue: Long
    ): Modifier {
        return then(
            GraphParentData(value.stepToGraphSize(maxValue))
        )
    }
}

fun Long.stepToGraphSize(max: Long) = if (this >= max) 300f else this / (max / 300f)

class GraphParentData(
    val value: Float
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any? =
        this@GraphParentData

}


@Composable
@Preview
fun PreviewUserSteps() = PreviewStepWalkTheme {
    UserPager(
        uiState = HomeUiState(
            steps = listOf(
                Step(
                    distance = 2000,
                    start = 120,
                    end = 160,
                    type = METs.Walk
                ),
                Step(
                    distance = 500,
                    start = 120,
                    end = 160,
                    type = METs.Walk
                ),
                Step(
                    distance = 800,
                    start = 120,
                    end = 160,
                    type = METs.Walk
                )
            ),
            user = User.getInitValues()
        )
    )
}