package com.stepmate.home.screen.homeUserBody

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stepmate.core.SnackBarMessage
import com.stepmate.design.component.DefaultOutlinedTextField
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.component.StepMateBoxDefaultTopBar
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.component.layout.DefaultLayout
import com.stepmate.design.theme.StepMateTheme

@Composable
internal fun HomeUserBodyScreen(
    homeUserBodyViewModel: HomeUserBodyViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    val age by homeUserBodyViewModel.age.collectAsStateWithLifecycle()
    val weight by homeUserBodyViewModel.weight.collectAsStateWithLifecycle()
    val height by homeUserBodyViewModel.height.collectAsStateWithLifecycle()

    HomeUserBodyScreen(
        age = age,
        weight = weight,
        height = height,
        updateAge = homeUserBodyViewModel::updateAge,
        updateWeight = homeUserBodyViewModel::updateWeight,
        updateHeight = homeUserBodyViewModel::updateHeight,
        navigateToHome = navigateToHome,
        showSnackBar = showSnackBar,
    )
}

@Composable
private fun HomeUserBodyScreen(
    age: Int,
    weight: Int,
    height: Int,
    updateAge: (Int) -> Unit,
    updateWeight: (Int) -> Unit,
    updateHeight: (Int) -> Unit,
    navigateToHome: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    DefaultLayout(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    focusManager.clearFocus()
                }
            ),
        topBar = {
            StepMateBoxDefaultTopBar(
                modifier = Modifier
                    .shadow(4.dp, RectangleShape, clip = false)
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.statusBars),
                icon = com.stepmate.design.R.drawable.ic_arrow_right_small,
                iconAlignment = Alignment.CenterEnd,
                onClick = {
                    if (age == 0 || weight == 0 || height == 0)
                        showSnackBar(
                            SnackBarMessage(
                                headerMessage = "신체정보를 입력해 주세요.",
                                contentMessage = "신체정보가 없으면 정확한 칼로리를 계산할 수 없어요."
                            )
                        )
                    else
                        navigateToHome()
                },
            )
        }
    ) {
        DescriptionLargeText(text = "StepMate 에 오신걸 환영해요.")
        VerticalSpacer(height = 20.dp)
        DescriptionSmallText(text = "서비스 이용에 앞서 정확한 칼로리 소모량 계산을 위해 신체정보를 수집하고 있어요.")
        VerticalSpacer(height = 4.dp)
        DescriptionSmallText(text = "신체 정보는 회원가입을 하지 않은 경우 회원님의 로컬 경로에 저장되며, StepMate는 이를 절대로 수집하지 않으니 걱정하지 않으셔도 되요.")
        VerticalSpacer(height = 4.dp)
        DescriptionSmallText(text = "회원 가입을 할 경우 회원님의 정보를 저장하기 위해 전송되지만, 비밀번호 및 신체정보는 모두 암호화 하여 저장하고 있어요.")
        VerticalSpacer(height = 50.dp)

        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            BodyInfo(
                hint = "나이 입력하기",
                text = age.toString(),
                updateValue = updateAge,
            )
            VerticalSpacer(height = 30.dp)
            BodyInfo(
                hint = "몸무게 입력하기",
                text = weight.toString(),
                updateValue = updateWeight,
            )
            VerticalSpacer(height = 30.dp)
            BodyInfo(
                hint = "키 입력하기",
                text = height.toString(),
                updateValue = updateHeight,
            )
        }
    }
}

@Composable
internal fun BodyInfo(
    hint: String,
    text: String,
    updateValue: (Int) -> Unit,
) {
    var textState by remember {
        mutableStateOf("").apply {
            if (text != "0")
                value = text
        }
    }

    DefaultOutlinedTextField(
        modifier = Modifier,
        informationText = hint,
        value = textState,
        onNewValue = { value ->
            textState = value
            if (value.isNotEmpty())
                updateValue(value.toInt())
        },
        keyboardType = KeyboardType.Decimal,
        trailingIcon = {
            Icon(
                painter = painterResource(id = com.stepmate.design.R.drawable.ic_arrow_right_small),
                contentDescription = null
            )
        }
    )
}

@Composable
@Preview
private fun PreviewHomeUserBodyScreen() = StepMateTheme {
    HomeUserBodyScreen(
        age = 0,
        weight = 60,
        height = 0,
        updateAge = {},
        updateWeight = {},
        updateHeight = {},
        navigateToHome = {},
        showSnackBar = {},
    )
}