package jinproject.stepwalk.mission.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.design.R

@Composable
internal fun MissonItem(
    title : String,
    @DrawableRes image : Int,
    height : Dp,
    contentColor : Color,
    containerColor : Color,
    navigateToMissonDetail : (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(contentColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = ImageVector.vectorResource(image),
            contentDescription = "미션 이미지",
            modifier = Modifier
                .fillMaxWidth()
                .height(height.div(1.5f)),
            contentScale = ContentScale.FillBounds,
            colorFilter = ColorFilter.tint(Color.Gray, BlendMode.Darken)
        )
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = containerColor
            )
        }
    }

}

@Composable
@Preview(widthDp = 150)
private fun PreviewMissonItem(

) = StepWalkTheme {
    MissonItem(
        title = "주간 미션",
        image = R.drawable.ic_fire,
        height = 200.dp,
        contentColor = StepWalkColor.blue_200.color,
        containerColor = StepWalkColor.blue_400.color,
        navigateToMissonDetail = {}
    )
}