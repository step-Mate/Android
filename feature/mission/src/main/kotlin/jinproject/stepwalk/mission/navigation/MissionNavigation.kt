package jinproject.stepwalk.mission.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import jinproject.stepwalk.mission.screen.mission.MissionScreen
import jinproject.stepwalk.mission.screen.missondetail.MissionDetailScreen

const val missionGraph = "missionGraph"
private const val missionRoute = "mission"
private const val missionDetailRoute = "missionDetail"
private const val missionDetailLink = "$missionDetailRoute?title={title}"

fun NavGraphBuilder.missionNavGraph(
    navigateToMissionDetail : (String) -> Unit,
    popBackStack: () -> Unit
) {
    navigation(
        startDestination = missionGraph,
        route = missionRoute
    ){
        composable(
            route = missionGraph
        ){
            MissionScreen(
                navigateToMissionDetail = navigateToMissionDetail
            )
        }

        composable(
            route = missionDetailLink
        ){
            MissionDetailScreen(

            )
        }
    }
}

fun NavController.navigateToMission(navOptions: NavOptions?){
    this.navigate(missionRoute, navOptions = navOptions)
}

fun NavController.navigateToMissionDetail(title : String){
    this.navigate("$missionDetailRoute?title={$title}"){
        launchSingleTop = true
    }
}