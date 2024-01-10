package jinproject.stepwalk.mission.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import jinproject.stepwalk.mission.screen.MissonScreen

const val missonRoute = "misson"

fun NavGraphBuilder.missonNavigation(
    popBackStack: () -> Unit
) {
    composable(
        route = missonRoute
    ){
        MissonScreen(

        )
    }
}

fun NavController.navigateToMisson(navOptions: NavOptions?){
    this.navigate(missonRoute, navOptions = navOptions)
}