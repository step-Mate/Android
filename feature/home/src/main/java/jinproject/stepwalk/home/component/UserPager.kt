package jinproject.stepwalk.home.component

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import jinproject.stepwalk.home.HomeUiState
import jinproject.stepwalk.home.MenuDetail
import jinproject.stepwalk.home.User
import jinproject.stepwalk.home.state.HealthState
import jinproject.stepwalk.home.state.Page
import jinproject.stepwalk.home.state.Step
import jinproject.stepwalk.home.utils.toAchievementDegree
import jinproject.stepwalk.design.PreviewStepWalkTheme
import jinproject.stepwalk.design.component.HorizontalSpacer
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.domain.METs
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
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
            figure = uiState.steps.distance.toInt(),
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
                uiState.steps.setMenuDetails(55f)
                MenuDetails(details = uiState.steps.details)
            }

            Page.HeartRate -> {

            }

            Page.DrinkWater -> {

            }
        }

        VerticalSpacer(height = 20.dp)
        MenuDetailGraph(
            itemsCount = 24,
            modifier = Modifier,
            horizontalAxis = {
                StepGraphTail()
            },
            verticalAxis = {
                StepGraphHeader()
            },
            bar = { index ->


            }
        )
    }
}

@Composable
private fun StepGraphTail() {
    Row {
        (0..23).forEach { hour ->
            Text(
                text = hour.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun StepGraphHeader() {
    Column {

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
    bar: @Composable (Int) -> Unit,
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

        val totalWidth = horizontalPlacable.width

        val barPlacable = barMeasurables.map { barMeasurable ->
            val barParentData = barMeasurable.parentData as GraphParentData
            val barHeight = (barParentData.value * 10).roundToInt()

            val barPlacable = barMeasurable.measure(
                constraints.copy(
                    minHeight = barHeight,
                    maxHeight = barHeight
                )
            )

            barPlacable
        }

        val totalHeight = horizontalPlacable.height + barPlacable.maxOf { it.height }

        layout(totalWidth, totalHeight) {
            var xPos = verticalPlacable.width
            val yPos = horizontalPlacable.height

            verticalPlacable.place(0, yPos)
            horizontalPlacable.place(xPos, 0)

            barPlacable.forEach { placeable ->
                placeable.place(xPos, yPos)
                xPos += placeable.width
            }
        }
    }
}

@LayoutScopeMarker
@Immutable
object GraphScope {
    @Stable
    fun Modifier.setGraphParentData(
        value: Float
    ): Modifier {
        return then(
            GraphParentData(
                value = value
            )
        )
    }
}

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
            steps = Step(
                distance = 2000,
                minutes = 120,
                type = METs.Walk
            ),
            user = User.getInitValues()
        )
    )
}