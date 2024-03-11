package jinproject.stepwalk.home.screen.homeSetting

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.HorizontalDivider
import jinproject.stepwalk.design.component.HorizontalWeightSpacer
import jinproject.stepwalk.design.component.StepMateNumberPicker
import jinproject.stepwalk.design.component.StepMateTopBar
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.component.clickableAvoidingDuplication
import jinproject.stepwalk.design.component.layout.DefaultLayout
import jinproject.stepwalk.design.theme.StepWalkTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun HomeSettingScreen(
    homeSettingViewModel: HomeSettingViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    HomeSettingScreen(
        setStepGoal = homeSettingViewModel::setStepGoal,
        popBackStack = popBackStack,
        showSnackBar = showSnackBar,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HomeSettingScreen(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    setStepGoal: (Int) -> Unit,
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    val pagerItems = remember {
        (0..50000 step 100).toList()
    }

    val pagerState = rememberPagerState(50) {
        50000 / 100 + 1
    }

    var isSheetVisible by remember {
        mutableStateOf(false)
    }

    StepMateNumberPicker(
        isSheetVisible = isSheetVisible,
        items = pagerItems,
        pagerState = pagerState,
        sheetState = sheetState,
        updateIsSheetVisibility = { bool -> isSheetVisible = bool }
    ) {
        val goal = pagerItems[pagerState.currentPage]

        setStepGoal(goal)
        showSnackBar(
            SnackBarMessage(
                headerMessage = "걸음수 목표치가 설정되었어요.",
                contentMessage = "목표치: [$goal]"
            )
        )
    }

    DefaultLayout(
        topBar = {
            StepMateTopBar(
                icon = R.drawable.ic_arrow_left_small,
                onClick = popBackStack,
            )
        }
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(vertical = 12.dp, horizontal = 8.dp),
        ) {
            DescriptionLargeText(
                text = "목표치 설정",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            VerticalSpacer(height = 4.dp)

            VerticalSpacer(height = 32.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .clickableAvoidingDuplication {
                        coroutineScope.launch {
                            isSheetVisible = true
                            sheetState.expand()
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                DescriptionLargeText(
                    text = "걸음수 목표치 설정",
                    modifier = Modifier,
                )
                HorizontalWeightSpacer(float = 1f)
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right_small),
                    contentDescription = "RightArrowIcon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            VerticalSpacer(height = 8.dp)
            HorizontalDivider()
        }
    }
}

@Composable
@Preview
private fun PreviewHomeSettingScreen() = StepWalkTheme {
    HomeSettingScreen(
        setStepGoal = {},
        popBackStack = {},
        showSnackBar = {},
    )
}