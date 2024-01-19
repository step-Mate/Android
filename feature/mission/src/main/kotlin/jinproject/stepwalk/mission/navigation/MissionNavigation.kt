package jinproject.stepwalk.mission.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import jinproject.stepwalk.mission.screen.mission.MissionScreen
import jinproject.stepwalk.mission.screen.missiondetail.missiontime.MissionTimeScreen
import jinproject.stepwalk.mission.screen.missiondetail.missonrepeat.MissionRepeatScreen
import jinproject.stepwalk.mission.screen.mission.state.MissionMode
import jinproject.stepwalk.mission.screen.mission.state.toMissionMode

const val missionGraph = "missionGraph"
private const val missionRoute = "mission"
private const val missionDetailRoute = "missionDetail"
private const val missionDetailLink = "$missionDetailRoute/{title}/{missionType}"

fun NavGraphBuilder.missionNavGraph(
    navigateToMissionDetail : (String, MissionMode) -> Unit,
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
            route = missionDetailLink,
            arguments = listOf(
                navArgument("title"){
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("missionType"){
                    type = NavType.IntType
                }
            )
        ){nav ->
            when(nav.arguments?.getInt("missionType")?.toMissionMode() ?: MissionMode.time){
                MissionMode.time -> {
                    MissionTimeScreen()
                }
                MissionMode.repeat -> {
                    MissionRepeatScreen()
                }
            }
        }
    }
}

fun NavController.navigateToMission(navOptions: NavOptions?){
    this.navigate(missionRoute, navOptions = navOptions)
}

fun NavController.navigateToMissionDetail(title : String,mode : MissionMode){
    this.navigate("$missionDetailRoute/$title/${mode.ordinal}"){
        launchSingleTop = true
    }
}