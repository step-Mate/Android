package com.stepmate.login.screen.information

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stepmate.design.R
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.component.SelectionButton
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.theme.StepWalkColor
import com.stepmate.login.screen.component.EnableButton
import com.stepmate.login.screen.component.LoginLayout

@Composable
internal fun InformationScreen(
    navigateToTerms: () -> Unit,
    navigateToSignUp: () -> Unit,
    popBackStack: () -> Unit
) {
    var termsState by remember { mutableStateOf(false) }

    LoginLayout(
        text = "이용 안내",
        content = {
            Column(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        1.dp,
                        color = MaterialTheme.colorScheme.onSurface,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                DescriptionLargeText(
                    modifier = Modifier.padding(start = 10.dp, top = 10.dp),
                    text = "서비스 이용 안내"
                )
                DescriptionSmallText(
                    modifier = Modifier.padding(start = 10.dp, top = 5.dp, end = 10.dp),
                    text = "스텝워크는 고객님의 원활한 서비스 이용 및 정보 보호를 위해 일부 서비스에 대한 이용 약관 동의를 받고 있습니다. 아래 내용을 확인후 동의해 주시기 바랍니다."
                )
                VerticalSpacer(height = 20.dp)
            }
            Box(
                modifier = Modifier
                    .clickable(onClick = navigateToTerms)
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SelectionButton(
                        buttonStatus = termsState,
                        modifier = Modifier
                            .clickable { termsState = !termsState }
                            .width(60.dp)
                            .height(30.dp),
                    )
                    DescriptionLargeText(
                        text = "서비스 이용 약관",
                        modifier = Modifier
                            .padding(start = 20.dp)
                    )
                    DescriptionSmallText(
                        text = "(필수)",
                        color = StepWalkColor.blue_400.color,
                        modifier = Modifier
                            .padding(start = 5.dp)
                    )
                }
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right_small),
                    contentDescription = "right arrow",
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .align(Alignment.CenterEnd),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            VerticalSpacer(height = 30.dp)
        },
        bottomContent = {
            EnableButton(
                text = "다음",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = termsState,
                onClick = navigateToSignUp
            )
        },
        popBackStack = popBackStack
    )
}

@Preview()
@Composable
private fun InformationPreview(

) {
    InformationScreen(
        navigateToTerms = {},
        navigateToSignUp = {},
        popBackStack = {}
    )
}