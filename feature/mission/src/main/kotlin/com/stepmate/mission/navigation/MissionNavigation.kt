package com.stepmate.mission.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.stepmate.core.slideLeftIn
import com.stepmate.core.slideRightOut
import com.stepmate.mission.screen.mission.MissionScreen
import com.stepmate.mission.screen.missiondetail.MissionDetailScreen


const val missionGraph = "missionGraph"
const val missionRoute = "mission"
private const val missionDetailRoute = "missionDetail"
private const val missionDetailLink = "$missionDetailRoute/{title}"

fun NavGraphBuilder.missionNavGraph(
    navigateToMissionDetail : (String) -> Unit,
    navigateToLogin : (NavOptions?) -> Unit,
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
                navigateToMissionDetail = navigateToMissionDetail,
                navigateToLogin = navigateToLogin
            )
        }

        composable(
            route = missionDetailLink,
            enterTransition = slideLeftIn(500),
            exitTransition = slideRightOut(700),
            arguments = listOf(
                navArgument("title"){
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ){
            MissionDetailScreen(
                popBackStack = popBackStack
            )
        }
    }
}

fun NavController.navigateToMission(navOptions: NavOptions?){
    this.navigate(missionRoute, navOptions = navOptions)
}

fun NavController.navigateToMissionDetail(title : String){
    this.navigate("$missionDetailRoute/$title"){
        launchSingleTop = true
    }
}
