package jinproject.stepwalk.home.screen.home.component.tab.menu

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.HorizontalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.home.HomeUiState
import jinproject.stepwalk.home.screen.home.HomeUiStatePreviewParameters
import jinproject.stepwalk.home.screen.home.state.MenuItem

@Composable
internal fun MenuDetails(
    menuList: List<MenuItem>,
    configuration: Configuration = LocalConfiguration.current,
) {
    val menuCounter = if (menuList.isEmpty()) 3 else menuList.size
    val cardSpacerSize = (menuCounter - 1) * 24
    val cardSize = (configuration.screenWidthDp - cardSpacerSize - 16) / menuCounter

    Row(modifier = Modifier.fillMaxWidth()) {
        menuList.forEachIndexed { index, item ->
            Surface(
                modifier = Modifier.size(cardSize.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondary,
                tonalElevation = 10.dp,
                shadowElevation = 10.dp
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(id = item.img),
                        contentDescription = "IconDetail"
                    )
                    Text(
                        text = when (item.intro.contains("분")) {
                            true -> item.value.toInt().toString()
                            false -> String.format("%.2f", item.value)
                        },
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = item.intro,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (index != menuList.lastIndex)
                HorizontalSpacer(width = 24.dp)
        }
    }
}

@Composable
@Preview
private fun PreviewMenuDetails(
    @PreviewParameter(HomeUiStatePreviewParameters::class, 1) homeUiState: HomeUiState,
) = StepWalkTheme {
    MenuDetails(
        menuList = homeUiState.step.menu
    )
}