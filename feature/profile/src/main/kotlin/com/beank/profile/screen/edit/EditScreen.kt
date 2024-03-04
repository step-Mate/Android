package com.beank.profile.screen.edit

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
import jinproject.stepwalk.core.MAX_AGE_LENGTH
import jinproject.stepwalk.core.MAX_BODY_LENGTH
import jinproject.stepwalk.core.MAX_NICKNAME_LENGTH
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.DefaultButton
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.DefaultOutlinedTextField
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.component.StepMateBoxDefaultTopBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun EditScreen(
    editViewModel: EditViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    val state by editViewModel.saveState.collectAsStateWithLifecycle()
    val uiState by editViewModel.uiState.collectAsStateWithLifecycle(initialValue = EditViewModel.UiState.Loading)
    LaunchedEffect(key1 = state) {
        if (state)
            popBackStack()
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
        nickname = editViewModel.nickname,
        designation = editViewModel.designation,
        designationList = editViewModel.designationList,
        nicknameValid = editViewModel.nicknameValid,
        ageValid = editViewModel.ageValid,
        heightValid = editViewModel.heightValid,
        weightValid = editViewModel.weightValid,
        age = editViewModel.age,
        height = editViewModel.height,
        weight = editViewModel.weight,
        loginState = editViewModel.anonymousState,
        onEvent = editViewModel::onEvent,
        popBackStack = popBackStack
    )
}

@Composable
private fun EditScreen(
    nickname: StateFlow<String>,
    designation: StateFlow<String>,
    designationList: StateFlow<List<String>>,
    nicknameValid: StateFlow<Valid>,
    ageValid: StateFlow<Boolean>,
    heightValid: StateFlow<Boolean>,
    weightValid: StateFlow<Boolean>,
    age: StateFlow<String>,
    height: StateFlow<String>,
    weight: StateFlow<String>,
    loginState: Boolean,
    onEvent: (EditUserEvent) -> Unit,
    popBackStack: () -> Unit
) {
    val nicknameValidState by nicknameValid.collectAsStateWithLifecycle()
    val ageValidState by ageValid.collectAsStateWithLifecycle()
    val heightValidState by heightValid.collectAsStateWithLifecycle()
    val weightValidState by weightValid.collectAsStateWithLifecycle()
    val nicknameState by nickname.collectAsStateWithLifecycle()
    val designationState by designation.collectAsStateWithLifecycle()
    val designationListState by designationList.collectAsStateWithLifecycle()
    val ageState by age.collectAsStateWithLifecycle()
    val heightState by height.collectAsStateWithLifecycle()
    val weightState by weight.collectAsStateWithLifecycle()

    DefaultLayout(
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
        Column(
            modifier = Modifier
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            if (!loginState) {
                DefaultOutlinedTextField(
                    modifier = Modifier.padding(top = 20.dp),
                    informationText = "닉네임",
                    errorMessage = if (nicknameValidState == Valid.Duplication) "중복된 닉네임이 존재합니다."
                    else "한글,영어,숫자가능,특수문자불가,2~10글자까지 입력가능",
                    value = nicknameState,
                    isError = nicknameValidState != Valid.Success
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
                            text = designationState,
                            textAlign = TextAlign.Center
                        )
                    }
                    items(items = designationListState, key = { it }) { designation ->
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
                value = ageState,
                isError = ageValidState,
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
                value = heightState,
                isError = heightValidState,
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
                value = weightState,
                isError = weightValidState,
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 20.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            DefaultButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = {
                    onEvent(EditUserEvent.Save)
                },
                enabled = !(nicknameValidState != Valid.Success && ageValidState && heightValidState && weightValidState),
                backgroundColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(5.dp)
            ) {
                DescriptionLargeText(
                    text = "저장",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = if (!(nicknameValidState != Valid.Success && ageValidState && heightValidState && weightValidState)) 1f else 0.3f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun EditPreview(

) {
    EditScreen(
        nickname = MutableStateFlow(""),
        designation = MutableStateFlow("test21312"),
        designationList = MutableStateFlow(listOf("test", "test12", "testset")),
        nicknameValid = MutableStateFlow(Valid.Success),
        ageValid = MutableStateFlow(false),
        heightValid = MutableStateFlow(false),
        weightValid = MutableStateFlow(false),
        age = MutableStateFlow(""),
        height = MutableStateFlow(""),
        weight = MutableStateFlow(""),
        loginState = false,
        onEvent = {}
    ) {

    }
}