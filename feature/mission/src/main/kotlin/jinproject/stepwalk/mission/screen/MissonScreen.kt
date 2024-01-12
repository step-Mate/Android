package jinproject.stepwalk.mission.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.mission.component.MissonItem

@Composable
internal fun MissonScreen(
    missonViewModel: MissonViewModel = hiltViewModel()
) {

}

@Composable
private fun MissonScreen(

){
    DefaultLayout(
        contentPaddingValues = PaddingValues(vertical = 20.dp, horizontal = 12.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ){
            items(arrayListOf("")) {
//                MissonItem(
//
//                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewMissonScreen(

) = StepWalkTheme {
    MissonScreen(

    )
}