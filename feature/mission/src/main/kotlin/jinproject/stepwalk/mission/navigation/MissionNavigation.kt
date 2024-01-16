package jinproject.stepwalk.mission.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import jinproject.stepwalk.mission.screen.MissionScreen
import jinproject.stepwalk.mission.screen.missondetail.MissionDetailScreen

const val missionRoute = "mission"
private const val missionDetailRoute = "missionDetail"

fun NavGraphBuilder.missionNavGraph(
    popBackStack: () -> Unit
) {
    composable(
        route = missionRoute
    ){
        MissionScreen(

        )
    }

    composable(
        route = missionDetailRoute
    ){
        MissionDetailScreen(

        )
    }
}

fun NavController.navigateToMission(navOptions: NavOptions?){
    this.navigate(missionRoute, navOptions = navOptions)
}

fun NavController.navigateToMissionDetail(){
    this.navigate(missionDetailRoute)
}