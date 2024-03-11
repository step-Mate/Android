package jinproject.stepwalk.home.screen.homeUserBody

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import jinproject.stepwalk.design.appendColorText
import jinproject.stepwalk.design.component.DescriptionAnnotatedLargeText
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.HorizontalWeightSpacer
import jinproject.stepwalk.design.component.StepMateBoxDefaultTopBar
import jinproject.stepwalk.design.component.StepMateNumberPicker
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.component.clickableAvoidingDuplication
import jinproject.stepwalk.design.component.layout.DefaultLayout
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.navigation.homeUserBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun HomeUserBodyScreen(
    homeUserBodyViewModel: HomeUserBodyViewModel = hiltViewModel(),
    navigateToHome: (NavOptions?) -> Unit,
) {
    val age by homeUserBodyViewModel.age.collectAsStateWithLifecycle()
    val weight by homeUserBodyViewModel.weight.collectAsStateWithLifecycle()
    val height by homeUserBodyViewModel.height.collectAsStateWithLifecycle()
    val bottomSheetType by homeUserBodyViewModel.bottomSheetType.collectAsStateWithLifecycle()

    HomeUserBodyScreen(
        age = age,
        weight = weight,
        height = height,
        bottomSheetType = bottomSheetType,
        updateAge = homeUserBodyViewModel::updateAge,
        updateWeight = homeUserBodyViewModel::updateWeight,
        updateHeight = homeUserBodyViewModel::updateHeight,
        updateSheetType = homeUserBodyViewModel::updateBottomSheetType,
        navigateToHome = navigateToHome,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun HomeUserBodyScreen(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    age: Int,
    weight: Int,
    height: Int,
    bottomSheetType: HomeUserBodyViewModel.BottomSheetType,
    updateAge: (Int) -> Unit,
    updateWeight: (Int) -> Unit,
    updateHeight: (Int) -> Unit,
    updateSheetType: (HomeUserBodyViewModel.BottomSheetType) -> Unit,
    navigateToHome: (NavOptions?) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    val pagerItems by rememberUpdatedState(
        newValue = when (bottomSheetType) {
            HomeUserBodyViewModel.BottomSheetType.Age -> (12..100).toList()
            HomeUserBodyViewModel.BottomSheetType.Weight -> (20..200).toList()
            HomeUserBodyViewModel.BottomSheetType.Height -> (100..250).toList()
        }
    )

    val pagerState = rememberPagerState(
        when (bottomSheetType) {
            HomeUserBodyViewModel.BottomSheetType.Age -> 8
            HomeUserBodyViewModel.BottomSheetType.Weight -> 50
            HomeUserBodyViewModel.BottomSheetType.Height -> 70
        }
    ) {
        pagerItems.size
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
        val value = pagerItems[pagerState.currentPage]

        when (bottomSheetType) {
            HomeUserBodyViewModel.BottomSheetType.Age -> updateAge(value)
            HomeUserBodyViewModel.BottomSheetType.Weight -> updateWeight(value)
            HomeUserBodyViewModel.BottomSheetType.Height -> updateHeight(value)
        }
    }

    DefaultLayout(
        topBar = {
            StepMateBoxDefaultTopBar(
                modifier = Modifier
                    .shadow(4.dp, RectangleShape, clip = false)
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.statusBars),
                icon = jinproject.stepwalk.design.R.drawable.ic_arrow_right_small,
                iconAlignment = Alignment.CenterEnd,
                onClick = {
                    navigateToHome(navOptions {
                        popUpTo(homeUserBody) {
                            inclusive = true
                        }
                    })
                },
            )
        }
    ) {
        DescriptionLargeText(text = "StepMate 에 오신걸 환영해요.")
        VerticalSpacer(height = 20.dp)
        DescriptionSmallText(text = "서비스 이용에 앞서 정확한 칼로리 소모량 계산을 위해 신체정보를 수집하고 있어요.")
        VerticalSpacer(height = 4.dp)
        DescriptionSmallText(text = "신체 정보는 회원가입을 하지 않은 경우 회원님의 로컬 경로에 저장되며, StepMate는 이를 절대로 수집하지 않으니 걱정하지 않으셔도 되요.")
        VerticalSpacer(height = 50.dp)

        BodyInfo(
            value = age,
            bottomSheetType = HomeUserBodyViewModel.BottomSheetType.Age,
            onClick = {
                coroutineScope.launch {
                    updateSheetType(HomeUserBodyViewModel.BottomSheetType.Age)
                    isSheetVisible = true
                    sheetState.expand()
                }
            },
        )
        VerticalSpacer(height = 30.dp)
        BodyInfo(
            value = weight,
            bottomSheetType = HomeUserBodyViewModel.BottomSheetType.Weight,
            onClick = {
                coroutineScope.launch {
                    updateSheetType(HomeUserBodyViewModel.BottomSheetType.Weight)
                    isSheetVisible = true
                    sheetState.expand()
                }
            },
        )
        VerticalSpacer(height = 30.dp)
        BodyInfo(
            value = height,
            bottomSheetType = HomeUserBodyViewModel.BottomSheetType.Height,
            onClick = {
                coroutineScope.launch {
                    updateSheetType(HomeUserBodyViewModel.BottomSheetType.Height)
                    isSheetVisible = true
                    sheetState.expand()
                }
            },
        )

    }
}

@Composable
internal fun BodyInfo(
    value: Int,
    bottomSheetType: HomeUserBodyViewModel.BottomSheetType,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .clickableAvoidingDuplication {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val infoText = buildAnnotatedString {
            if (value == 0)
                append("${bottomSheetType.display} 입력하기")
            else {
                append("$value ")
                appendColorText(
                    text = when (bottomSheetType) {
                        HomeUserBodyViewModel.BottomSheetType.Age -> "세"
                        HomeUserBodyViewModel.BottomSheetType.Weight -> "kg"
                        HomeUserBodyViewModel.BottomSheetType.Height -> "cm"
                    }, color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            DescriptionAnnotatedLargeText(text = infoText)
            HorizontalWeightSpacer(float = 1f)
            Icon(
                painter = painterResource(id = jinproject.stepwalk.design.R.drawable.ic_arrow_right_small),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
@Preview
private fun PreviewHomeUserBodyScreen() = StepWalkTheme {
    HomeUserBodyScreen(
        age = 0,
        weight = 60,
        height = 0,
        bottomSheetType = HomeUserBodyViewModel.BottomSheetType.Age,
        updateAge = {},
        updateWeight = {},
        updateHeight = {},
        updateSheetType = {},
        navigateToHome = {},
    )
}