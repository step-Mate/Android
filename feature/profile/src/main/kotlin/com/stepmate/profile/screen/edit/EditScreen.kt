package com.stepmate.profile.screen.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stepmate.core.MAX_AGE_LENGTH
import com.stepmate.core.MAX_BODY_LENGTH
import com.stepmate.core.MAX_NICKNAME_LENGTH
import com.stepmate.core.SnackBarMessage
import com.stepmate.design.R
import com.stepmate.design.component.DefaultButton
import com.stepmate.design.component.DefaultOutlinedTextField
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.component.HeadlineText
import com.stepmate.design.component.StepMateBoxDefaultTopBar
import com.stepmate.design.component.layout.DefaultLayout

@Composable
internal fun EditScreen(
    editViewModel: EditViewModel = hiltViewModel(),
    navigateToProfile: () -> Unit,
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    val state by editViewModel.saveState.collectAsStateWithLifecycle()
    val uiState by editViewModel.uiState.collectAsStateWithLifecycle(initialValue = EditViewModel.UiState.Loading)
    val nicknameValid by editViewModel.nicknameValid.collectAsStateWithLifecycle()
    val ageValid by editViewModel.ageValid.collectAsStateWithLifecycle()
    val heightValid by editViewModel.heightValid.collectAsStateWithLifecycle()
    val weightValid by editViewModel.weightValid.collectAsStateWithLifecycle()
    val nickname by editViewModel.nickname.collectAsStateWithLifecycle()
    val designation by editViewModel.designation.collectAsStateWithLifecycle()
    val designationList by editViewModel.designationList.collectAsStateWithLifecycle()
    val age by editViewModel.age.collectAsStateWithLifecycle()
    val height by editViewModel.height.collectAsStateWithLifecycle()
    val weight by editViewModel.weight.collectAsStateWithLifecycle()
    val snackBarMessage by editViewModel.snackBarState.collectAsStateWithLifecycle(
        initialValue = SnackBarMessage.getInitValues()
    )
    LaunchedEffect(key1 = snackBarMessage) {
        if (snackBarMessage.headerMessage.isNotBlank())
            showSnackBar(snackBarMessage)
    }
    LaunchedEffect(key1 = state) {
        if (state)
            navigateToProfile()
    }
    LaunchedEffect(key1 = uiState) {
        if (uiState is EditViewModel.UiState.Error)
            showSnackBar(
                SnackBarMessage(
                    "칭호 정보를 불러오지 못했습니다."
                )
            )
    }

    EditScreen(
        nickname = nickname,
        designation = designation,
        designationList = designationList,
        nicknameValid = nicknameValid,
        ageValid = ageValid,
        heightValid = heightValid,
        weightValid = weightValid,
        age = age,
        height = height,
        weight = weight,
        loginState = editViewModel.anonymousState,
        onEvent = editViewModel::onEvent,
        popBackStack = popBackStack
    )
}

@Composable
private fun EditScreen(
    nickname: String,
    designation: String,
    designationList: List<String>,
    nicknameValid: Valid,
    ageValid: Boolean,
    heightValid: Boolean,
    weightValid: Boolean,
    age: String,
    height: String,
    weight: String,
    loginState: Boolean,
    onEvent: (EditUserEvent) -> Unit,
    popBackStack: () -> Unit
) {


    DefaultLayout(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState()),
        contentPaddingValues = PaddingValues(horizontal = 12.dp),
        topBar = {
            StepMateBoxDefaultTopBar(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.statusBars),
                icon = R.drawable.ic_arrow_left_small,
                onClick = popBackStack
            ) {
                HeadlineText(text = "내 정보 수정", modifier = Modifier.align(Alignment.Center))
            }
        }
    ) {
        Column {
            if (!loginState) {
                DefaultOutlinedTextField(
                    modifier = Modifier.padding(top = 20.dp),
                    informationText = "닉네임",
                    errorMessage = if (nicknameValid == Valid.Duplication) "중복된 닉네임이 존재합니다."
                    else "한글,영어,숫자가능,특수문자불가,2~10글자까지 입력가능",
                    value = nickname,
                    isError = nicknameValid != Valid.Success
                ) {
                    val text = it.trim()
                    if (text.length <= MAX_NICKNAME_LENGTH)
                        onEvent(EditUserEvent.Nickname(text))
                }

                HeadlineText(
                    modifier = Modifier.padding(start = 5.dp, bottom = 5.dp),
                    text = "칭호"
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    state = rememberLazyListState(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    item {
                        DescriptionLargeText(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp, start = 2.dp, end = 2.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(5.dp)
                                )
                                .padding(vertical = 10.dp),
                            text = designation,
                            textAlign = TextAlign.Center
                        )
                    }
                    items(items = designationList, key = { it }) { designation ->
                        DescriptionLargeText(
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .fillMaxWidth()
                                .clickable {
                                    onEvent(EditUserEvent.Designation(designation))
                                },
                            text = designation,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            DefaultOutlinedTextField(
                modifier = Modifier.padding(top = 20.dp),
                informationText = "나이",
                errorMessage = "정확한 나이를 입력해주세요.",
                value = age,
                isError = ageValid,
                keyboardType = KeyboardType.NumberPassword
            ) {
                val text = it.trim()
                if (text.length <= MAX_AGE_LENGTH) {
                    onEvent(EditUserEvent.Age(text))
                }
            }

            DefaultOutlinedTextField(
                informationText = "키",
                errorMessage = "정확한 키를 입력해주세요.",
                value = height,
                isError = heightValid,
                keyboardType = KeyboardType.NumberPassword,
                suffix = {
                    DescriptionSmallText(text = "cm")
                }
            ) {
                val text = it.trim()
                if (text.length <= MAX_BODY_LENGTH) {
                    onEvent(EditUserEvent.Height(text))
                }
            }

            DefaultOutlinedTextField(
                informationText = "몸무게",
                errorMessage = "정확한 몸무게를 입력해주세요.",
                value = weight,
                isError = weightValid,
                keyboardType = KeyboardType.NumberPassword,
                suffix = {
                    DescriptionSmallText(text = "kg")
                }
            ) {
                val text = it.trim()
                if (text.length <= MAX_BODY_LENGTH) {
                    onEvent(EditUserEvent.Weight(text))
                }
            }
        }

        DefaultButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .height(50.dp),
            onClick = {
                onEvent(EditUserEvent.Save)
            },
            enabled = (nicknameValid == Valid.Success && !ageValid && !heightValid && !weightValid),
            backgroundColor = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(5.dp)
        ) {
            DescriptionLargeText(
                text = "저장",
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = if (!(nicknameValid != Valid.Success && ageValid && heightValid && weightValid)) 1f else 0.3f)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewEditLogin(
) {
    EditScreen(
        nickname = "",
        designation = "test21312",
        designationList = listOf("test", "test12", "testset"),
        nicknameValid = Valid.Success,
        ageValid = false,
        heightValid = false,
        weightValid = false,
        age = "",
        height = "",
        weight = "",
        loginState = false,
        onEvent = {}
    ) {
    }
}

@Preview
@Composable
private fun PreviewEditAnonymous(
) {
    EditScreen(
        nickname = "",
        designation = "test21312",
        designationList = listOf("test", "test12", "testset"),
        nicknameValid = Valid.Success,
        ageValid = false,
        heightValid = false,
        weightValid = false,
        age = "",
        height = "",
        weight = "",
        loginState = true,
        onEvent = {}
    ) {
    }
}