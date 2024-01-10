package jinproject.stepwalk.login.component

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DefaultButton
import jinproject.stepwalk.design.component.DefaultTextButton
import jinproject.stepwalk.design.component.HorizontalSpacer

//간편 로그인용
@Composable
internal fun IconButton(
    @DrawableRes icon : Int,
    containerColor : Color,
    simbolColor : Color,
    labelColor : Color,
    @StringRes text : Int,
    onClick : () -> Unit
) {
    Box(
        modifier = Modifier
            .fieldModifier()
            .clickable(onClick = onClick)
            .background(containerColor, RoundedCornerShape(12))
            .clip(RoundedCornerShape(12)),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(icon),
                contentDescription = "icon button",
                tint = simbolColor,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = text),
                    style = MaterialTheme.typography.bodyMedium,
                    color = labelColor,
                )
            }
        }
    }
}

@Composable
internal fun FindAndSignUpButtons(
    findAccountId : () -> Unit,
    findAccountPassword : () -> Unit,
    createAccount : () -> Unit,
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 12.dp)
    ) {
        DefaultTextButton(
            text = "아이디 찾기",
            textColor = MaterialTheme.colorScheme.scrim,
            onClick = findAccountId
        )
        HorizontalSpacer(width = 10.dp)
        GrayVerticalDivider(modifier = Modifier.height(15.dp))
        HorizontalSpacer(width = 10.dp)
        DefaultTextButton(
            text = "비밀번호 찾기",
            textColor = MaterialTheme.colorScheme.scrim,
            onClick = findAccountPassword
        )
        HorizontalSpacer(width = 10.dp)
        GrayVerticalDivider(modifier = Modifier.height(15.dp))
        HorizontalSpacer(width = 10.dp)
        DefaultTextButton(
            text = "회원가입",
            textColor = MaterialTheme.colorScheme.scrim,
            onClick = createAccount
        )
    }
}


@Composable
internal fun EnableButton(
    text: String,
    modifier: Modifier,
    enabled : Boolean = false,
    onClick: () -> Unit
){
    DefaultButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        backgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = if(enabled) 1f else 0.5f),
        shape = RoundedCornerShape(5.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = if(enabled) 1f else 0.5f)
        )
    }
}




@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun IconButtonPreview() {
    Box(contentAlignment = Alignment.Center) {
        IconButton(
            icon = jinproject.stepwalk.design.R.drawable.ic_kakao_simbol,
            containerColor = Color.Yellow,
            simbolColor = Color.Black,
            labelColor = Color.Black,
            text = jinproject.stepwalk.design.R.string.kakao_login_button
        ) {

        }
    }

}