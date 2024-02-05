package jinproject.stepwalk.design.component.layout.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import jinproject.stepwalk.design.component.layout.asLoose
import java.time.LocalDateTime
import java.time.ZoneOffset

@Composable
fun HealthChartLayout(
    itemsCount: Int,
    modifier: Modifier = Modifier,
    horizontalAxis: @Composable (Int) -> Unit,
    verticalAxis: @Composable () -> Unit,
    bar: @Composable (Int) -> Unit,
    header: @Composable () -> Unit,
    popUp: @Composable () -> Unit,
    popUpState: PopUpState,
) {
    val bars = @Composable { repeat(itemsCount) { bar(it) } }

    val horizontalItemCount = if (itemsCount >= 16) itemsCount / 2 else itemsCount
    val horizontalStep = if (itemsCount == horizontalItemCount) 1 else 2
    val horizontalItemList = (0 until itemsCount step horizontalStep).toList()
    val horizontalItemConversion =
        if (itemsCount == 7) horizontalItemList.sortDayOfWeek() else horizontalItemList
    val horizontalItems = @Composable {
        horizontalItemConversion.forEach { h -> horizontalAxis(h) }
    }

    Layout(
        contents = listOf(horizontalItems, verticalAxis, bars, header, popUp),
        modifier = modifier
    ) { (horizontalMeasurables, verticalMeasurables, barMeasurables, headerMeasurables, popUpMeasurables), constraints ->

        require(verticalMeasurables.size == 1) {
            "세로축은 반드시 하나만 존재해야 함"
        }
        val totalHeight = constraints.maxHeight
        val totalWidth = constraints.maxWidth

        val loosedConstraints = constraints.asLoose()

        val headerPlaceable = headerMeasurables.first().measure(loosedConstraints)

        val popUpText = popUpMeasurables.first().parentData as HealthPopUpData
        val popUpPlaceable = popUpMeasurables.first().measure(
            loosedConstraints.copy(
                minWidth = popUpText.figure.length * 20 + 35,
                maxWidth = popUpText.figure.length * 20 + 35,
                minHeight = 55,
                maxHeight = 55,
            )
        )

        val verticalPlacable = verticalMeasurables.first().measure(
            loosedConstraints.copy(
                maxHeight = totalHeight - headerPlaceable.height,
                minHeight = totalHeight - headerPlaceable.height
            )
        )

        val itemWidth = (totalWidth - verticalPlacable.width) / horizontalItemCount

        val horizontalPlacables = horizontalMeasurables.map { measurable ->
            measurable.measure(
                loosedConstraints.copy(
                    maxWidth = itemWidth,
                    minWidth = itemWidth,
                )
            )
        }

        val barWidth = if (itemsCount == horizontalItemCount) itemWidth else itemWidth / 2

        val barMaxData = barMeasurables.maxOf { (it.parentData as HealthChartData).height }
        val barDatas = barMeasurables.map { (it.parentData as HealthChartData).height.toInt() }
        val barMaxHeight = totalHeight - horizontalPlacables.first().height - headerPlaceable.height

        val barPlaceables = barMeasurables.map { barMeasurable ->
            barMeasurable.measure(
                loosedConstraints.copy(
                    minHeight = barMaxHeight,
                    maxHeight = barMaxHeight,
                    minWidth = barWidth,
                    maxWidth = barWidth
                )
            )
        }

        layout(totalWidth, totalHeight) {
            var xPos = verticalPlacable.width

            headerPlaceable.place(0, 0)

            verticalPlacable.place(
                0,
                headerPlaceable.height
            )

            val eachBarWidth = horizontalPlacables.first().width / horizontalStep

            barPlaceables.forEachIndexed { index, placeable ->

                if (index % 2 == 0 || itemsCount == horizontalItemCount) {
                    val horizontalPlaceable = horizontalPlacables[index / horizontalStep]
                    horizontalPlaceable.place(xPos, totalHeight - horizontalPlaceable.height)
                }

                placeable.place(
                    xPos,
                    totalHeight - horizontalPlacables.first().height - placeable.height
                )

                xPos += eachBarWidth
            }

            barDatas.getOrNull(popUpState.index)?.let { data ->
                val barHeight =
                    data.stepToSizeByMax(barHeight = barMaxHeight.toFloat(), barMaxData).toInt()

                popUpPlaceable.place(
                    verticalPlacable.width + eachBarWidth * (popUpState.index) + eachBarWidth / 2,
                    totalHeight - horizontalPlacables.first().height - barHeight - popUpPlaceable.height - 20
                )
            }
        }
    }
}

internal fun <T : Number> Number.stepToSizeByMax(barHeight: Float, max: T) =
    when {
        this.toInt() == 0 -> 0f

        this.toFloat() >= max.toFloat() -> barHeight

        else -> barHeight / (max.toFloat() / this.toFloat())
    }

/**
 * 이번주에서 오늘이 가장 마지막에 위치하도록 값들을 sort 하는 함수
 *
 * *반드시 주단위로 정렬된 상태이어야 함
 * @exception IllegalArgumentException : 리스트가 비어있거나, 크기가 7을 초과하는 경우
 * @return 오늘이 가장 마지막인 7개의 요일 리스트
 */
fun <T : Number> List<T>.sortDayOfWeek() = run {
    if (this.size > 7 || this.isEmpty())
        throw IllegalArgumentException("비어있는 리스트 이거나 size가 7을 초과함")

    val today = LocalDateTime.now().atZone(ZoneOffset.of("+9")).dayOfWeek.value
    val arrayList = ArrayList<T>(7)

    val subListBigger = this.filterIndexed { index, _ -> index + 1 > today }
    val subListSmaller = this.filterIndexed { index, _ -> index + 1 <= today }

    arrayList.apply {
        addAll(subListBigger)
        addAll(subListSmaller)
    }
}