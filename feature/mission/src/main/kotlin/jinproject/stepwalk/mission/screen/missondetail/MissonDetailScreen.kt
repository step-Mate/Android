package jinproject.stepwalk.mission.screen.missondetail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.theme.StepWalkTheme

@Composable
internal fun MissonDetailScreen(
    missonDetailViewModel: MissonDetailViewModel = hiltViewModel()
) {

    MissonDetailScreen(

    )
}

@Composable
private fun MissonDetailScreen(

){
    DefaultLayout(
        contentPaddingValues = PaddingValues(vertical = 20.dp, horizontal = 12.dp)
    ) {

    }
}

@Composable
@Preview
private fun PreviewMissonDetail(

) = StepWalkTheme {
    MissonDetailScreen(

    )
}