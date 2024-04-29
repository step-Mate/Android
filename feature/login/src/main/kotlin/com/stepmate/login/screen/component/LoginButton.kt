package com.stepmate.login.screen.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stepmate.design.R
import com.stepmate.design.component.DefaultButton
import com.stepmate.design.component.DefaultTextButton
import com.stepmate.design.component.VerticalDivider
import com.stepmate.design.theme.StepWalkColor
import com.stepmate.design.theme.StepMateTheme

//간편 로그인용
@Composable
internal fun IconButton(
    @DrawableRes icon: Int,
    containerColor: Color,
    simbolColor: Color,
    labelColor: Color,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(5.dp))
            .background(containerColor)
            .padding(horizontal = 15.dp, vertical = 10.dp),
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(icon),
            contentDescription = "icon button",
            tint = simbolColor,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = labelColor,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
internal fun FindAndSignUpButtons(
    findAccountId: () -> Unit,
    findAccountPassword: () -> Unit,
    createAccount: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        DefaultTextButton(
            text = "아이디 찾기",
            onClick = findAccountId,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        VerticalDivider(modifier = Modifier.padding(vertical = 10.dp))
        DefaultTextButton(
            text = "비밀번호 찾기",
            onClick = findAccountPassword,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        VerticalDivider(modifier = Modifier.padding(vertical = 10.dp))
        DefaultTextButton(
            text = "회원가입",
            onClick = createAccount,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
    }
}


@Composable
internal fun EnableButton(
    text: String,
    modifier: Modifier,
    enabled: Boolean = false,
    loading: Boolean = false,
    onClick: () -> Unit
) {
    DefaultButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        backgroundColor = MaterialTheme.colorScheme.primary,
        shape = RoundedCornerShape(5.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.height(IntrinsicSize.Min),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = if (enabled) 1f else 0.3f)
            )
        }
    }
}


@Composable
@Preview
private fun PreviewIconButton(

) = StepMateTheme {
    IconButton(
        icon = R.drawable.ic_kakao_simbol,
        containerColor = StepWalkColor.kakao_yellow.color,
        simbolColor = StepWalkColor.kakao_black.color,
        labelColor = StepWalkColor.kakao_black.color,
        text = "카카오톡 간편로그인"
    ) {
    }
}

@Composable
@Preview
private fun PreviewFindAndSignUp(

) = StepMateTheme {
    FindAndSignUpButtons(
        findAccountId = { },
        findAccountPassword = { }) {
    }
}

@Composable
@Preview
private fun PreviewEnableButton(

) = StepMateTheme {
    EnableButton(
        text = "Preview",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        enabled = true,
        loading = true
    ) {

    }
}

@Composable
@Preview
private fun PreviewNotEnableButton(

) = StepMateTheme {
    EnableButton(
        text = "Preview",
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        enabled = false
    ) {

    }
}


