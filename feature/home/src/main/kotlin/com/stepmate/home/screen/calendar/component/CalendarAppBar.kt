package com.stepmate.home.screen.calendar.component

import android.graphics.Color
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stepmate.design.R
import com.stepmate.design.component.AppBarText
import com.stepmate.design.component.DefaultIconButton
import com.stepmate.design.component.StepMateBoxDefaultTopBar
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.home.screen.calendar.CalendarData
import com.stepmate.home.screen.home.state.Day
import com.stepmate.home.screen.home.state.Month
import com.stepmate.home.screen.home.state.Week
import com.stepmate.home.screen.home.state.Year

@Composable
internal fun CalendarAppBar(
    calendarData: CalendarData,
    popBackStack: () -> Unit,
    setCalendarData: (CalendarData) -> Unit,
) {
    val time = calendarData.selectedTime
    val type by rememberUpdatedState(newValue = calendarData.type)
    val text = when (type) {
        Day -> "${time.year}년 ${time.monthValue}월"
        Month -> "${time.year}년"
        Year -> "년도를 선택해 주세요."
        Week -> throw IllegalArgumentException("주단위 달력은 존재 하지 않음.")
    }
    val prevType = when (type) {
        Day -> Month
        Month -> Year
        Year -> Year
        Week -> throw IllegalArgumentException("${calendarData.type} 에서는 전환 불가. (년도 ~ 월) 까지만 전환 가능")
    }

    StepMateBoxDefaultTopBar(
        modifier = Modifier
            .shadow(4.dp, RectangleShape, clip = false)
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars),
        icon = R.drawable.ic_arrow_left_small,
        onClick = popBackStack,
    ) {
        AppBarText(
            text = text,
            modifier = Modifier
                .align(Alignment.Center)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    enabled = calendarData.type == Day || calendarData.type == Month,
                    indication = null,
                ) {
                    setCalendarData(
                        calendarData.copy(
                            type = prevType,
                        )
                    )
                }
        )

        val bool = when (type) {
            Month -> calendarData.selectedTime.monthValue != 0
            Year -> calendarData.selectedTime.year != 0
            else -> false
        }

        val alpha by animateFloatAsState(
            targetValue = if (bool) 1f else 0f,
            label = "SelectionButtonAlpha"
        )

        DefaultIconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .graphicsLayer {
                    this.alpha = alpha
                },
            icon = R.drawable.ic_check,
            onClick = {
                when (type) {
                    Month -> setCalendarData(calendarData.copy(type = Day))
                    Year -> setCalendarData(calendarData.copy(type = Month))
                    else -> {}
                }
            },
            iconTint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = Color.WHITE.toLong())
private fun PreviewCalendarAppBar() = StepMateTheme {
    CalendarAppBar(
        calendarData = CalendarData.getInitValues(),
        popBackStack = {},
        setCalendarData = {},
    )
}