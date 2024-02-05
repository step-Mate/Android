package jinproject.stepwalk.mission.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import jinproject.stepwalk.domain.model.MissionMode
import jinproject.stepwalk.mission.screen.mission.MissionScreen
import jinproject.stepwalk.mission.screen.missiondetail.missonrepeat.MissionRepeatScreen


const val missionGraph = "missionGraph"
const val missionRoute = "mission"
private const val missionDetailRoute = "missionDetail"
private const val missionDetailLink = "$missionDetailRoute/{title}/{mode}"

fun NavGraphBuilder.missionNavGraph(
    navigateToMissionDetail : (String,MissionMode) -> Unit,
    popBackStack: () -> Unit
) {
    navigation(
        startDestination = missionRoute,
        route = missionGraph
    ){
        composable(
            route = missionRoute
        ){
            MissionScreen(
                navigateToMissionDetail = navigateToMissionDetail
            )
        }

        composable(
            route = missionDetailLink,
            arguments = listOf(
                navArgument("title"){
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("mode"){
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ){
            MissionRepeatScreen(
                popBackStack = popBackStack
            )
        }
    }
}

fun NavController.navigateToMission(navOptions: NavOptions?){
    this.navigate(missionRoute, navOptions = navOptions)
}

fun NavController.navigateToMissionDetail(title : String, mode : MissionMode){
    this.navigate("$missionDetailRoute/$title/${mode.ordinal}"){
        launchSingleTop = true
    }
}
